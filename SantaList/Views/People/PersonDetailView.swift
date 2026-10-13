import SwiftUI
import SwiftData

/// Màn hình chi tiết người nhận quà: xem danh sách quà, sửa/xóa và quản lý ngân sách cá nhân
struct PersonDetailView: View {
    @Environment(\.modelContext) private var modelContext
    @Environment(\.dismiss) private var dismiss

    @Bindable var person: Person

    @State private var showingEditPersonSheet = false
    @State private var showingAddGiftSheet = false
    @State private var giftToEdit: Gift? = nil

    @State private var giftToDelete: Gift? = nil
    @State private var showingDeleteGiftAlert = false

    @State private var showingDeletePersonAlert = false

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Thẻ tóm tắt ngân sách người nhận
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(person.name)
                                .font(.title2.weight(.bold))
                                .foregroundStyle(Color("TextPrimary"))

                            if !person.note.isEmpty {
                                Text(person.note)
                                    .font(.subheadline)
                                    .foregroundStyle(Color("TextSecondary"))
                            }
                        }

                        Spacer()

                        Button {
                            showingEditPersonSheet = true
                        } label: {
                            Image(systemName: "pencil.circle.fill")
                                .font(.title2)
                                .foregroundStyle(Color("PineGreen"))
                        }
                    }

                    Divider()

                    // Các chỉ số ngân sách
                    HStack(spacing: 12) {
                        BudgetMiniMetric(
                            title: "Ngân sách",
                            amount: person.budget,
                            color: Color("PineGreen")
                        )
                        BudgetMiniMetric(
                            title: "Dự kiến",
                            amount: person.plannedAmount,
                            color: Color("TextPrimary")
                        )
                        BudgetMiniMetric(
                            title: "Còn lại",
                            amount: person.remainingAmount,
                            color: person.isOverBudget ? Color("CranberryRed") : Color("PineGreen")
                        )
                    }

                    // Thanh tiến độ
                    BudgetProgressBar(
                        planned: person.plannedAmount,
                        budget: person.budget
                    )
                }
                .padding()
                .background(Color("AppCard"))
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .shadow(color: Color.black.opacity(0.04), radius: 8, x: 0, y: 3)
                .padding(.horizontal)

                // Danh sách quà tặng
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text("Quà tặng (\(person.gifts.count))")
                            .font(.headline)
                            .foregroundStyle(Color("TextPrimary"))

                        Spacer()

                        Button {
                            showingAddGiftSheet = true
                        } label: {
                            Label("Thêm quà", systemImage: "plus.circle.fill")
                                .font(.subheadline.weight(.semibold))
                                .foregroundStyle(Color("PineGreen"))
                        }
                    }
                    .padding(.horizontal)

                    if person.gifts.isEmpty {
                        VStack(spacing: 12) {
                            Image(systemName: "gift")
                                .font(.system(size: 44))
                                .foregroundStyle(Color("PineGreen").opacity(0.4))
                            Text("Chưa có món quà nào cho \(person.name)")
                                .font(.headline)
                            Text("Nhấn nút Thêm quà để bắt đầu lên danh sách quà tặng nhé!")
                                .font(.caption)
                                .foregroundStyle(Color("TextSecondary"))
                                .multilineTextAlignment(.center)
                            Button("Thêm quà ngay") {
                                showingAddGiftSheet = true
                            }
                            .buttonStyle(.borderedProminent)
                            .tint(Color("PineGreen"))
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 36)
                        .background(Color("AppCard"))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .padding(.horizontal)
                    } else {
                        LazyVStack(spacing: 12) {
                            ForEach(person.gifts.sorted { ($0.dueDate ?? .distantFuture) < ($1.dueDate ?? .distantFuture) }) { gift in
                                GiftRowCard(
                                    gift: gift,
                                    onEdit: {
                                        giftToEdit = gift
                                    },
                                    onDelete: {
                                        giftToDelete = gift
                                        showingDeleteGiftAlert = true
                                    },
                                    onStatusNext: {
                                        advanceStatus(for: gift)
                                    },
                                    onStatusSelect: { newStatus in
                                        setStatus(for: gift, newStatus: newStatus)
                                    }
                                )
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            }
            .padding(.vertical)
        }
        .background(Color("AppBackground"))
        .navigationTitle(person.name)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .destructiveAction) {
                Button(role: .destructive) {
                    showingDeletePersonAlert = true
                } label: {
                    Image(systemName: "trash")
                        .foregroundStyle(Color("CranberryRed"))
                }
            }
        }
        .sheet(isPresented: $showingEditPersonSheet) {
            AddEditPersonSheet(personToEdit: person)
        }
        .sheet(isPresented: $showingAddGiftSheet) {
            AddEditGiftSheet(preselectedPerson: person)
        }
        .sheet(item: $giftToEdit) { gift in
            AddEditGiftSheet(giftToEdit: gift, preselectedPerson: person)
        }
        .confirmationDialog(
            "Xác nhận xóa quà tặng",
            isPresented: $showingDeleteGiftAlert,
            titleVisibility: .visible,
            presenting: giftToDelete
        ) { gift in
            Button("Xóa quà \"\(gift.title)\"", role: .destructive) {
                deleteGift(gift)
            }
            Button("Hủy", role: .cancel) {}
        } message: { gift in
            Text("Bạn có chắc chắn muốn xóa món quà \"\(gift.title)\" không?")
        }
        .confirmationDialog(
            "Xóa người nhận",
            isPresented: $showingDeletePersonAlert,
            titleVisibility: .visible
        ) {
            Button("Xóa người nhận & toàn bộ quà", role: .destructive) {
                deletePerson()
            }
            Button("Hủy", role: .cancel) {}
        } message: {
            Text("Xóa \(person.name) sẽ xóa toàn bộ các món quà liên quan. Hành động này không thể hoàn tác.")
        }
    }

    private func advanceStatus(for gift: Gift) {
        gift.status = gift.status.next()
        try? modelContext.save()
        ReminderService.shared.scheduleReminder(for: gift)
    }

    private func setStatus(for gift: Gift, newStatus: GiftStatus) {
        gift.status = newStatus
        try? modelContext.save()
        ReminderService.shared.scheduleReminder(for: gift)
    }

    private func deleteGift(_ gift: Gift) {
        ReminderService.shared.cancelReminder(for: gift.id)
        person.gifts.removeAll { $0.id == gift.id }
        modelContext.delete(gift)
        try? modelContext.save()
    }

    private func deletePerson() {
        for gift in person.gifts {
            ReminderService.shared.cancelReminder(for: gift.id)
        }
        modelContext.delete(person)
        try? modelContext.save()
        dismiss()
    }
}

/// Thẻ nhỏ hiển thị chỉ số ngân sách
struct BudgetMiniMetric: View {
    let title: String
    let amount: Int
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(title)
                .font(.caption2)
                .foregroundStyle(Color("TextSecondary"))
            Text(Formatters.formatVND(amount))
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(color)
                .lineLimit(1)
                .minimumScaleFactor(0.8)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(8)
        .background(color.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

/// Dòng hiển thị quà tặng có tương tác chip, sửa và vuốt xóa
struct GiftRowCard: View {
    let gift: Gift
    let onEdit: () -> Void
    let onDelete: () -> Void
    let onStatusNext: () -> Void
    let onStatusSelect: (GiftStatus) -> Void

    var body: some View {
        HStack(alignment: .center, spacing: 14) {
            // Icon dịp lễ
            Image(systemName: gift.occasion.systemImage)
                .font(.title3)
                .foregroundStyle(Color("PineGreen"))
                .frame(width: 36, height: 36)
                .background(Color("PineGreen").opacity(0.12))
                .clipShape(Circle())

            VStack(alignment: .leading, spacing: 4) {
                Text(gift.title)
                    .font(.body.weight(.semibold))
                    .foregroundStyle(Color("TextPrimary"))

                HStack(spacing: 8) {
                    Text(Formatters.formatVND(gift.price))
                        .font(.subheadline.weight(.medium))
                        .foregroundStyle(Color("PineGreen"))

                    if let due = gift.dueDate {
                        Text("•")
                            .foregroundStyle(Color("TextSecondary"))

                        if gift.isOverdue() {
                            Text("Trễ hạn (\(Formatters.dueDateString(for: due)))")
                                .font(.caption2.weight(.bold))
                                .foregroundStyle(Color("CranberryRed"))
                        } else {
                            Text(Formatters.dueDateString(for: due))
                                .font(.caption2)
                                .foregroundStyle(Color("TextSecondary"))
                        }
                    }
                }
            }

            Spacer()

            // Chip trạng thái
            StatusChipView(
                status: gift.status,
                onNext: onStatusNext,
                onSelect: onStatusSelect
            )
        }
        .padding()
        .background(Color("AppCard"))
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .contentShape(Rectangle())
        .onTapGesture {
            onEdit()
        }
        .contextMenu {
            Button {
                onEdit()
            } label: {
                Label("Sửa món quà", systemImage: "pencil")
            }

            Button(role: .destructive) {
                onDelete()
            } label: {
                Label("Xóa món quà", systemImage: "trash")
            }
        }
        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
            Button(role: .destructive) {
                onDelete()
            } label: {
                Label("Xóa", systemImage: "trash")
            }
        }
    }
}
