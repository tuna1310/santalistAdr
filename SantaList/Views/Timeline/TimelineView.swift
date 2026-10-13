import SwiftUI
import SwiftData

/// Màn hình dòng thời gian quà tặng (Tab 2)
struct TimelineView: View {
    @Environment(\.modelContext) private var modelContext
    @Query private var gifts: [Gift]
    @Query private var people: [Person]

    @State private var showingAddGiftSheet = false
    @State private var giftToEdit: Gift? = nil
    @State private var giftToDelete: Gift? = nil
    @State private var showingDeleteAlert = false

    @State private var selectedFilter: TimelineFilter = .all

    enum TimelineFilter: String, CaseIterable, Identifiable {
        case all = "Tất cả"
        case pending = "Cần chuẩn bị"
        case overdue = "Trễ hạn"
        case completed = "Đã xong"

        var id: String { rawValue }
    }

    /// Quà sắp theo hạn tăng dần, quà không có hạn nằm ở cuối
    private var sortedGifts: [Gift] {
        gifts.sorted { gift1, gift2 in
            switch (gift1.dueDate, gift2.dueDate) {
            case let (d1?, d2?):
                return d1 < d2
            case (_?, nil):
                return true
            case (nil, _?):
                return false
            case (nil, nil):
                return gift1.title < gift2.title
            }
        }
    }

    private var filteredGifts: [Gift] {
        switch selectedFilter {
        case .all:
            return sortedGifts
        case .pending:
            return sortedGifts.filter { !$0.status.isCompleted }
        case .overdue:
            return sortedGifts.filter { $0.isOverdue() }
        case .completed:
            return sortedGifts.filter { $0.status.isCompleted }
        }
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                AppHeaderView()

                // Bộ lọc nhanh danh mục quà
                Picker("Lọc quà", selection: $selectedFilter) {
                    ForEach(TimelineFilter.allCases) { filter in
                        Text(filter.rawValue).tag(filter)
                    }
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)
                .padding(.vertical, 8)
                .background(Color("AppBackground"))

                if gifts.isEmpty {
                    ContentUnavailableView {
                        Label("Chưa có món quà nào", systemImage: "calendar.badge.clock")
                    } description: {
                        Text("Dòng thời gian sẽ tự động sắp xếp các món quà theo hạn chót để bạn không bao giờ bỏ lỡ khoảnh khắc tặng quà nào.")
                    } actions: {
                        Button {
                            showingAddGiftSheet = true
                        } label: {
                            Text("Thêm quà ngay")
                                .fontWeight(.semibold)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color("PineGreen"))
                    }
                    .frame(maxHeight: .infinity)
                } else if filteredGifts.isEmpty {
                    ContentUnavailableView {
                        Label("Không có món quà phù hợp", systemImage: "line.3.horizontal.decrease.circle")
                    } description: {
                        Text("Không có món quà nào trong mục \"\(selectedFilter.rawValue)\".")
                    }
                    .frame(maxHeight: .infinity)
                } else {
                    ScrollView {
                        LazyVStack(spacing: 14) {
                            ForEach(filteredGifts) { gift in
                                TimelineGiftCard(
                                    gift: gift,
                                    onEdit: {
                                        giftToEdit = gift
                                    },
                                    onDelete: {
                                        giftToDelete = gift
                                        showingDeleteAlert = true
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
                        .padding()
                        .padding(.bottom, 60)
                    }
                }
            }
            .background(Color("AppBackground"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showingAddGiftSheet = true
                    } label: {
                        Label("Thêm quà", systemImage: "gift.fill")
                            .foregroundStyle(Color("PineGreen"))
                    }
                    .accessibilityLabel("Thêm món quà")
                }
            }
            .overlay(alignment: .bottomTrailing) {
                if !gifts.isEmpty {
                    Button {
                        showingAddGiftSheet = true
                    } label: {
                        HStack(spacing: 8) {
                            Image(systemName: "plus")
                                .font(.body.weight(.bold))
                            Text("Thêm quà")
                                .font(.subheadline.weight(.bold))
                        }
                        .foregroundStyle(.white)
                        .padding(.horizontal, 18)
                        .padding(.vertical, 12)
                        .background(Color("PineGreen"))
                        .clipShape(Capsule())
                        .shadow(color: Color("PineGreen").opacity(0.35), radius: 8, x: 0, y: 4)
                    }
                    .padding(.trailing, 20)
                    .padding(.bottom, 16)
                    .accessibilityLabel("Nút nổi thêm quà mới")
                }
            }
            .sheet(isPresented: $showingAddGiftSheet) {
                AddEditGiftSheet()
            }
            .sheet(item: $giftToEdit) { gift in
                AddEditGiftSheet(giftToEdit: gift)
            }
            .confirmationDialog(
                "Xác nhận xóa quà tặng",
                isPresented: $showingDeleteAlert,
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
        if let person = gift.person {
            person.gifts.removeAll { $0.id == gift.id }
        }
        modelContext.delete(gift)
        try? modelContext.save()
    }
}

/// Thẻ hiển thị quà tặng trong Dòng thời gian với chỉ báo hạn chót
struct TimelineGiftCard: View {
    let gift: Gift
    let onEdit: () -> Void
    let onDelete: () -> Void
    let onStatusNext: () -> Void
    let onStatusSelect: (GiftStatus) -> Void

    private var isOverdue: Bool {
        gift.isOverdue()
    }

    private var isCompleted: Bool {
        gift.status.isCompleted
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Thanh trạng thái hạn chót
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: dateIconName)
                        .font(.caption2.weight(.bold))

                    Text(dateBadgeText)
                        .font(.caption.weight(.semibold))
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 4)
                .background(badgeBackgroundColor.opacity(0.15))
                .foregroundStyle(badgeForegroundColor)
                .clipShape(Capsule())

                Spacer()

                if let person = gift.person {
                    HStack(spacing: 4) {
                        Image(systemName: "person.circle.fill")
                            .font(.caption)
                            .foregroundStyle(Color("PineGreen"))
                        Text(person.name)
                            .font(.caption.weight(.medium))
                            .foregroundStyle(Color("TextSecondary"))
                    }
                }
            }

            // Tiêu đề & giá tiền
            HStack(alignment: .center, spacing: 12) {
                Image(systemName: gift.occasion.systemImage)
                    .font(.title3)
                    .foregroundStyle(Color("PineGreen"))
                    .frame(width: 40, height: 40)
                    .background(Color("PineGreen").opacity(0.12))
                    .clipShape(Circle())

                VStack(alignment: .leading, spacing: 4) {
                    Text(gift.title)
                        .font(.headline)
                        .foregroundStyle(Color("TextPrimary"))

                    HStack(spacing: 8) {
                        Text(Formatters.formatVND(gift.price))
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(Color("PineGreen"))

                        Text("•")
                            .foregroundStyle(Color("TextSecondary"))

                        Text(gift.occasion.title)
                            .font(.caption)
                            .foregroundStyle(Color("TextSecondary"))
                    }
                }

                Spacer()

                // Chip trạng thái tương tác
                StatusChipView(
                    status: gift.status,
                    onNext: onStatusNext,
                    onSelect: onStatusSelect
                )
            }

            if !gift.note.isEmpty {
                Text(gift.note)
                    .font(.caption)
                    .foregroundStyle(Color("TextSecondary"))
                    .lineLimit(2)
                    .padding(.top, 2)
            }
        }
        .padding()
        .background(Color("AppCard"))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(isOverdue ? Color("CranberryRed").opacity(0.4) : Color.clear, lineWidth: 1.5)
        )
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

    private var dateIconName: String {
        if isOverdue {
            return "exclamationmark.triangle.fill"
        } else if isCompleted {
            return "checkmark.circle.fill"
        } else if gift.dueDate != nil {
            return "calendar"
        } else {
            return "calendar.badge.minus"
        }
    }

    private var dateBadgeText: String {
        if let due = gift.dueDate {
            if isOverdue {
                return "Trễ hạn • \(Formatters.dueDateString(for: due))"
            } else if isCompleted {
                return "Đã xong • \(Formatters.dueDateString(for: due))"
            } else {
                return "Hạn: \(Formatters.dueDateString(for: due))"
            }
        } else {
            return "Không có hạn"
        }
    }

    private var badgeBackgroundColor: Color {
        if isOverdue {
            return Color("CranberryRed")
        } else if isCompleted {
            return Color("PineGreen")
        } else {
            return Color("WarmGold")
        }
    }

    private var badgeForegroundColor: Color {
        if isOverdue {
            return Color("CranberryRed")
        } else if isCompleted {
            return Color("PineGreen")
        } else {
            return Color("WarmGold")
        }
    }
}
