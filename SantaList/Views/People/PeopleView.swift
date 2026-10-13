import SwiftUI
import SwiftData

/// Màn hình danh sách người nhận quà (Tab 1)
struct PeopleView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Person.name) private var people: [Person]

    @State private var showingAddPersonSheet = false
    @State private var showingAddGiftSheet = false
    @State private var personToEdit: Person? = nil
    @State private var personToDelete: Person? = nil
    @State private var showingDeleteAlert = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                AppHeaderView()

                if people.isEmpty {
                    ContentUnavailableView {
                        Label("Chưa có người nhận nào", systemImage: "person.crop.circle.badge.plus")
                    } description: {
                        Text("Hãy thêm những người thân yêu bạn muốn tặng quà dịp này để bắt đầu lên kế hoạch và quản lý ngân sách.")
                    } actions: {
                        Button {
                            showingAddPersonSheet = true
                        } label: {
                            Text("Thêm người nhận đầu tiên")
                                .fontWeight(.semibold)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color("PineGreen"))
                    }
                    .frame(maxHeight: .infinity)
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(people) { person in
                                NavigationLink {
                                    PersonDetailView(person: person)
                                } label: {
                                    PersonCard(
                                        person: person,
                                        onEdit: {
                                            personToEdit = person
                                        },
                                        onDelete: {
                                            personToDelete = person
                                            showingDeleteAlert = true
                                        }
                                    )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding()
                        .padding(.bottom, 60) // chừa khoảng trống cho floating button
                    }
                }
            }
            .background(Color("AppBackground"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        showingAddPersonSheet = true
                    } label: {
                        Label("Thêm người", systemImage: "person.badge.plus")
                            .foregroundStyle(Color("PineGreen"))
                    }
                    .accessibilityLabel("Thêm người nhận")
                }

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
                // Floating Action Button "Thêm quà"
                if !people.isEmpty {
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
            .sheet(isPresented: $showingAddPersonSheet) {
                AddEditPersonSheet()
            }
            .sheet(item: $personToEdit) { person in
                AddEditPersonSheet(personToEdit: person)
            }
            .sheet(isPresented: $showingAddGiftSheet) {
                AddEditGiftSheet()
            }
            .confirmationDialog(
                "Xác nhận xóa người nhận",
                isPresented: $showingDeleteAlert,
                titleVisibility: .visible,
                presenting: personToDelete
            ) { person in
                Button("Xóa người nhận & toàn bộ quà", role: .destructive) {
                    deletePerson(person)
                }
                Button("Hủy", role: .cancel) {}
            } message: { person in
                Text("Xóa \"\(person.name)\" sẽ xóa toàn bộ các món quà thuộc về người này. Hành động không thể hoàn tác.")
            }
        }
    }

    private func deletePerson(_ person: Person) {
        for gift in person.gifts {
            ReminderService.shared.cancelReminder(for: gift.id)
        }
        modelContext.delete(person)
        try? modelContext.save()
    }
}

/// Thẻ hiển thị tóm tắt một người nhận trong danh sách
struct PersonCard: View {
    let person: Person
    let onEdit: () -> Void
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(person.name)
                        .font(.headline)
                        .foregroundStyle(Color("TextPrimary"))

                    if !person.note.isEmpty {
                        Text(person.note)
                            .font(.caption)
                            .foregroundStyle(Color("TextSecondary"))
                            .lineLimit(1)
                    }
                }

                Spacer()

                Menu {
                    Button {
                        onEdit()
                    } label: {
                        Label("Sửa thông tin", systemImage: "pencil")
                    }

                    Button(role: .destructive) {
                        onDelete()
                    } label: {
                        Label("Xóa người nhận", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis")
                        .font(.body)
                        .foregroundStyle(Color("TextSecondary"))
                        .padding(8)
                }
            }

            // Tóm tắt quà và số tiền
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: "gift")
                        .font(.caption)
                        .foregroundStyle(Color("PineGreen"))
                    Text("\(person.gifts.count) món quà")
                        .font(.caption)
                        .foregroundStyle(Color("TextSecondary"))
                }

                Spacer()

                HStack(spacing: 4) {
                    Text("Dự kiến:")
                        .font(.caption2)
                        .foregroundStyle(Color("TextSecondary"))
                    Text(Formatters.formatVND(person.plannedAmount))
                        .font(.caption.weight(.bold))
                        .foregroundStyle(person.isOverBudget ? Color("CranberryRed") : Color("PineGreen"))
                    Text("/ \(Formatters.formatVND(person.budget))")
                        .font(.caption2)
                        .foregroundStyle(Color("TextSecondary"))
                }
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
        .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
    }
}
