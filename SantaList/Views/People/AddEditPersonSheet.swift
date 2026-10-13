import SwiftUI
import SwiftData

/// Sheet thêm mới hoặc chỉnh sửa người nhận
struct AddEditPersonSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.modelContext) private var modelContext

    var personToEdit: Person? = nil

    @State private var name: String = ""
    @State private var budgetText: String = ""
    @State private var note: String = ""

    @State private var isSubmitted: Bool = false

    private var isEditing: Bool {
        personToEdit != nil
    }

    private var trimmedName: String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var parsedBudget: Int? {
        let clean = budgetText.replacingOccurrences(of: ".", with: "")
            .replacingOccurrences(of: ",", with: "")
            .replacingOccurrences(of: " ", with: "")
            .replacingOccurrences(of: "₫", with: "")
        if clean.isEmpty { return 0 }
        return Int(clean)
    }

    private var nameValidationError: String? {
        if isSubmitted && trimmedName.isEmpty {
            return "Vui lòng nhập tên người nhận"
        }
        return nil
    }

    private var budgetValidationError: String? {
        guard let budget = parsedBudget else {
            return "Ngân sách phải là số nguyên dương"
        }
        if budget < 0 {
            return "Ngân sách không được nhỏ hơn 0"
        }
        return nil
    }

    private var isValid: Bool {
        !trimmedName.isEmpty && parsedBudget != nil && (parsedBudget ?? 0) >= 0
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Thông tin người nhận") {
                    VStack(alignment: .leading, spacing: 4) {
                        TextField("Họ và tên hoặc biệt danh", text: $name)
                            .textContentType(.name)

                        if let error = nameValidationError {
                            Text(error)
                                .font(.caption)
                                .foregroundStyle(Color("CranberryRed"))
                        }
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            Text("Ngân sách (VNĐ)")
                            Spacer()
                            TextField("0", text: $budgetText)
                                .keyboardType(.numberPad)
                                .multilineTextAlignment(.trailing)
                        }

                        if let error = budgetValidationError {
                            Text(error)
                                .font(.caption)
                                .foregroundStyle(Color("CranberryRed"))
                        }

                        if let budget = parsedBudget, budget > 0 {
                            Text("Tương đương: \(Formatters.formatVND(budget))")
                                .font(.caption2)
                                .foregroundStyle(Color("PineGreen"))
                        }
                    }
                }

                Section("Ghi chú") {
                    TextField("Sở thích, lưu ý, v.v. (tùy chọn)", text: $note, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle(isEditing ? "Sửa người nhận" : "Thêm người nhận")
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
                if let person = personToEdit {
                    name = person.name
                    budgetText = String(person.budget)
                    note = person.note
                }
            }
        }
    }

    private func save() {
        isSubmitted = true
        guard isValid else { return }

        let finalBudget = parsedBudget ?? 0

        if let person = personToEdit {
            person.name = trimmedName
            person.budget = finalBudget
            person.note = note
        } else {
            let newPerson = Person(
                name: trimmedName,
                budget: finalBudget,
                note: note
            )
            modelContext.insert(newPerson)
        }

        try? modelContext.save()
        dismiss()
    }
}
