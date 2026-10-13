import SwiftUI
import SwiftData
import Charts

/// Màn hình quản lý ngân sách và biểu đồ phân tích (Tab 3)
struct BudgetView: View {
    @Query(sort: \Person.name) private var people: [Person]
    @Query private var gifts: [Gift]

    private var totalBudget: Int {
        BudgetCalculator.totalBudget(people: people)
    }

    private var totalPlanned: Int {
        BudgetCalculator.totalPlanned(gifts: gifts)
    }

    private var totalSpent: Int {
        BudgetCalculator.totalSpent(gifts: gifts)
    }

    private var totalRemaining: Int {
        BudgetCalculator.totalRemaining(people: people, gifts: gifts)
    }

    private var isOverallOverBudget: Bool {
        BudgetCalculator.isOverBudget(people: people, gifts: gifts)
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                AppHeaderView()

                ScrollView {
                    VStack(spacing: 20) {
                        // 4 Thẻ chỉ số chính (KPI Cards)
                        LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 14) {
                            KPICard(
                                title: "Tổng ngân sách",
                                amount: totalBudget,
                                icon: "wallet.pass.fill",
                                color: Color("PineGreen")
                            )

                            KPICard(
                                title: "Dự kiến",
                                amount: totalPlanned,
                                icon: "list.clipboard.fill",
                                color: Color("WarmGold")
                            )

                            KPICard(
                                title: "Đã chi",
                                amount: totalSpent,
                                icon: "creditcard.fill",
                                color: Color.orange
                            )

                            KPICard(
                                title: "Còn lại",
                                amount: totalRemaining,
                                icon: isOverallOverBudget ? "exclamationmark.triangle.fill" : "checkmark.circle.fill",
                                color: isOverallOverBudget ? Color("CranberryRed") : Color("PineGreen")
                            )
                        }
                        .padding(.horizontal)

                        // Biểu đồ so sánh Ngân sách vs Dự kiến (Swift Charts)
                        if !people.isEmpty {
                            VStack(alignment: .leading, spacing: 14) {
                                HStack {
                                    Text("So sánh Ngân sách & Dự kiến")
                                        .font(.headline)
                                        .foregroundStyle(Color("TextPrimary"))

                                    Spacer()
                                }

                                Chart {
                                    ForEach(people) { person in
                                        BarMark(
                                            x: .value("Người nhận", person.name),
                                            y: .value("Số tiền", person.budget)
                                        )
                                        .foregroundStyle(by: .value("Khoản", "Ngân sách"))
                                        .position(by: .value("Khoản", "Ngân sách"))

                                        BarMark(
                                            x: .value("Người nhận", person.name),
                                            y: .value("Số tiền", person.plannedAmount)
                                        )
                                        .foregroundStyle(by: .value("Khoản", "Dự kiến"))
                                        .position(by: .value("Khoản", "Dự kiến"))
                                    }
                                }
                                .chartForegroundStyleScale([
                                    "Ngân sách": Color("PineGreen"),
                                    "Dự kiến": Color("WarmGold")
                                ])
                                .chartLegend(position: .top, alignment: .trailing)
                                .chartYAxis {
                                    AxisMarks(position: .leading) { value in
                                        AxisGridLine()
                                        AxisValueLabel {
                                            if let intVal = value.as(Int.self) {
                                                Text(formatCompactVND(intVal))
                                                    .font(.caption2)
                                            }
                                        }
                                    }
                                }
                                .frame(height: 220)
                            }
                            .padding()
                            .background(Color("AppCard"))
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
                            .padding(.horizontal)
                        }

                        // Danh sách tiến độ ngân sách từng người
                        VStack(alignment: .leading, spacing: 14) {
                            Text("Chi tiết theo từng người")
                                .font(.headline)
                                .foregroundStyle(Color("TextPrimary"))
                                .padding(.horizontal)

                            if people.isEmpty {
                                ContentUnavailableView {
                                    Label("Chưa có dữ liệu ngân sách", systemImage: "chart.bar.xaxis")
                                } description: {
                                    Text("Hãy thêm người nhận và đặt ngân sách để theo dõi tiến độ chi tiêu tại đây.")
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 30)
                            } else {
                                LazyVStack(spacing: 12) {
                                    ForEach(people) { person in
                                        VStack(alignment: .leading, spacing: 10) {
                                            HStack {
                                                Text(person.name)
                                                    .font(.subheadline.weight(.bold))
                                                    .foregroundStyle(Color("TextPrimary"))

                                                Spacer()

                                                Text(Formatters.formatVND(person.plannedAmount))
                                                    .font(.subheadline.weight(.semibold))
                                                    .foregroundStyle(person.isOverBudget ? Color("CranberryRed") : Color("PineGreen"))
                                                Text("/ \(Formatters.formatVND(person.budget))")
                                                    .font(.caption)
                                                    .foregroundStyle(Color("TextSecondary"))
                                            }

                                            BudgetProgressBar(
                                                planned: person.plannedAmount,
                                                budget: person.budget
                                            )
                                        }
                                        .padding()
                                        .background(Color("AppCard"))
                                        .clipShape(RoundedRectangle(cornerRadius: 14))
                                        .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 2)
                                    }
                                }
                                .padding(.horizontal)
                            }
                        }
                    }
                    .padding(.vertical)
                }
            }
            .background(Color("AppBackground"))
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    private func formatCompactVND(_ value: Int) -> String {
        if value >= 1_000_000 {
            let m = Double(value) / 1_000_000.0
            return String(format: "%.1fM", m)
        } else if value >= 1_000 {
            return "\(value / 1_000)k"
        } else {
            return "\(value)"
        }
    }
}

/// Thẻ hiển thị KPI ngân sách
struct KPICard: View {
    let title: String
    let amount: Int
    let icon: String
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Image(systemName: icon)
                    .font(.body.weight(.bold))
                    .foregroundStyle(color)

                Spacer()
            }

            VStack(alignment: .leading, spacing: 3) {
                Text(title)
                    .font(.caption)
                    .foregroundStyle(Color("TextSecondary"))

                Text(Formatters.formatVND(amount))
                    .font(.headline.weight(.bold))
                    .foregroundStyle(color)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
        }
        .padding()
        .background(Color("AppCard"))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
    }
}
