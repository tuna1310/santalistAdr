import SwiftUI

/// Thanh tiến độ ngân sách với hiệu ứng đổi sang đỏ khi vượt ngân sách
struct BudgetProgressBar: View {
    let planned: Int
    let budget: Int

    private var ratio: Double {
        guard budget > 0 else { return planned > 0 ? 1.0 : 0.0 }
        return Double(planned) / Double(budget)
    }

    private var isOver: Bool {
        planned > budget
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    // Thanh nền
                    Capsule()
                        .fill(Color.gray.opacity(0.18))
                        .frame(height: 8)

                    // Thanh tiến độ đã dự kiến
                    Capsule()
                        .fill(isOver ? Color("CranberryRed") : Color("PineGreen"))
                        .frame(width: max(0, min(geo.size.width * CGFloat(min(ratio, 1.0)), geo.size.width)), height: 8)
                        .animation(.easeInOut, value: ratio)
                }
            }
            .frame(height: 8)

            HStack {
                Text("\(Int(min(ratio * 100, 999)))% ngân sách")
                    .font(.caption2)
                    .foregroundStyle(isOver ? Color("CranberryRed") : Color("TextSecondary"))
                    .fontWeight(isOver ? .bold : .regular)

                Spacer()

                if isOver {
                    Text("Vượt \(Formatters.formatVND(planned - budget))")
                        .font(.caption2.weight(.bold))
                        .foregroundStyle(Color("CranberryRed"))
                } else {
                    Text("Còn \(Formatters.formatVND(budget - planned))")
                        .font(.caption2)
                        .foregroundStyle(Color("PineGreen"))
                }
            }
        }
    }
}
