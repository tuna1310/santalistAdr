import SwiftUI
import SwiftData

/// Sheet thêm mới hoặc chỉnh sửa món quà
struct AddEditGiftSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Person.name) private var people: [Person]

    var giftToEdit: Gift? = nil
    var preselectedPerson: Person? = nil

    @State private var selectedPerson: Person?
    @State private var title: String = ""
    @State private var priceText: String = ""
    @State private var hasDueDate: Bool = false
    @State private var dueDate: Date = Date()
    @State private var occasion: Occasion = .christmas
    @State private var status: GiftStatus = .idea
    @State private var note: String = ""
    @State private var url: String = ""

    @State private var isSubmitted: Bool = false

    private var isEditing: Bool {
        giftToEdit != nil
    }

    private var trimmedTitle: String {
        title.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var parsedPrice: Int? {
        let clean = priceText.replacingOccurrences(of: ".", with: "")
            .replacingOccurrences(of: ",", with: "")
            .replacingOccurrences(of: " ", with: "")
            .replacingOccurrences(of: "₫", with: "")
        if clean.isEmpty { return 0 }
        return Int(clean)
    }

    private var titleValidationError: String? {
        if isSubmitted && trimmedTitle.isEmpty {
            return "Vui lòng nhập tên món quà"
        }
        return nil
    }

    private var priceValidationError: String? {
        guard let price = parsedPrice else {
            return "Giá tiền phải là số nguyên không âm"
        }
        if price < 0 {
            return "Giá tiền không được nhỏ hơn 0"
        }
        return nil
    }

    private var personValidationError: String? {
        if isSubmitted && selectedPerson == nil {
            return "Vui lòng chọn người nhận quà"
        }
        return nil
    }

    private var isValid: Bool {
        !trimmedTitle.isEmpty && selectedPerson != nil && parsedPrice != nil && (parsedPrice ?? 0) >= 0
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Người nhận") {
                    if people.isEmpty {
                        Text("Chưa có người nhận nào. Vui lòng thêm người nhận trước.")
                            .font(.callout)
                            .foregroundStyle(Color("CranberryRed"))
                    } else {
                        Picker("Chọn người nhận", selection: $selectedPerson) {
                            Text("Chọn người nhận").tag(Person?.none)
                            ForEach(people) { person in
                                Text(person.name).tag(Person?.some(person))
                            }
                        }

                        if let error = personValidationError {
                            Text(error)
                                .font(.caption)
                                .foregroundStyle(Color("CranberryRed"))
                        }
                    }
                }

                Section("Thông tin món quà") {
                    VStack(alignment: .leading, spacing: 4) {
                        TextField("Tên món quà (bắt buộc)", text: $title)

                        if let error = titleValidationError {
                            Text(error)
                                .font(.caption)
                                .foregroundStyle(Color("CranberryRed"))
                        }
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            Text("Giá tiền (VNĐ)")
                            Spacer()
                            TextField("0", text: $priceText)
                                .keyboardType(.numberPad)
                                .multilineTextAlignment(.trailing)
                        }

                        if let error = priceValidationError {
                            Text(error)
                                .font(.caption)
                                .foregroundStyle(Color("CranberryRed"))
                        }

                        if let price = parsedPrice, price > 0 {
                            Text("Tương đương: \(Formatters.formatVND(price))")
                                .font(.caption2)
                                .foregroundStyle(Color("PineGreen"))
                        }
                    }

                    Picker("Dịp tặng", selection: $occasion) {
                        ForEach(Occasion.allCases) { occ in
                            Label(occ.title, systemImage: occ.systemImage).tag(occ)
                        }
                    }

                    Picker("Trạng thái", selection: $status) {
                        ForEach(GiftStatus.allCases) { st in
                            Label(st.title, systemImage: st.systemImage).tag(st)
                        }
                    }
                }

                Section("Hạn cần có") {
                    Toggle("Có thời hạn", isOn: $hasDueDate)

                    if hasDueDate {
                        DatePicker(
                            "Ngày hết hạn",
                            selection: $dueDate,
                            displayedComponents: .date
                        )
                        .datePickerStyle(.graphical)

                        Button(role: .destructive) {
                            hasDueDate = false
                        } label: {
                            Label("Bỏ hạn", systemImage: "xmark.circle")
                                .font(.callout)
                        }
                    }
                }

                Section("Chi tiết bổ sung") {
                    TextField("Đường dẫn mua hàng (https://...)", text: $url)
                        .keyboardType(.URL)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()

                    TextField("Ghi chú (kích thước, màu sắc, ý tưởng...)", text: $note, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle(isEditing ? "Sửa quà tặng" : "Thêm quà tặng")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Hủy") {
                        dismiss()
                    }
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button(isEditing ? "Lưu" : "Thêm") {
                        save()
                    }
                    .fontWeight(.semibold)
                    .foregroundStyle(Color("PineGreen"))
                }
            }
            .onAppear {
                setupInitialValues()
            }
        }
    }

    private func setupInitialValues() {
        if let gift = giftToEdit {
            title = gift.title
            priceText = String(gift.price)
            occasion = gift.occasion
            status = gift.status
            note = gift.note
            url = gift.url
            selectedPerson = gift.person
            if let due = gift.dueDate {
                hasDueDate = true
                dueDate = due
            } else {
                hasDueDate = false
                dueDate = Date()
            }
        } else {
            selectedPerson = preselectedPerson ?? people.first
            hasDueDate = false
            dueDate = Date()
        }
    }

    private func save() {
        isSubmitted = true
        guard isValid, let person = selectedPerson else { return }

        let finalPrice = parsedPrice ?? 0
        let finalDueDate = hasDueDate ? dueDate : nil

        if let gift = giftToEdit {
            let previousPerson = gift.person
            if previousPerson != person {
                previousPerson?.gifts.removeAll { $0.id == gift.id }
                gift.person = person
                person.gifts.append(gift)
            }
            gift.title = trimmedTitle
            gift.price = finalPrice
            gift.status = status
            gift.dueDate = finalDueDate
            gift.occasion = occasion
            gift.note = note
            gift.url = url

            ReminderService.shared.scheduleReminder(for: gift)
        } else {
            let newGift = Gift(
                title: trimmedTitle,
                price: finalPrice,
                status: status,
                dueDate: finalDueDate,
                occasion: occasion,
                note: note,
                url: url,
                person: person
            )
            person.gifts.append(newGift)
            modelContext.insert(newGift)

            ReminderService.shared.scheduleReminder(for: newGift)
        }

        try? modelContext.save()
        dismiss()
    }
}
