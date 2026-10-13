import SwiftUI

/// Header thương hiệu cho ứng dụng SantaList kèm đếm ngược Giáng sinh
struct AppHeaderView: View {
    @State private var currentDate = Date()
    private let timer = Timer.publish(every: 3600, on: .main, in: .common).autoconnect()

    var body: some View {
        VStack(spacing: 8) {
            HStack(alignment: .center) {
                VStack(alignment: .leading, spacing: 3) {
                    HStack(spacing: 6) {
                        Text("🎅")
                            .font(.title2)
                        Text("SantaList")
                            .font(.system(.title2, design: .rounded, weight: .bold))
                            .foregroundStyle(Color("PineGreen"))
                    }

                    Text("Quà đúng người, đúng hạn, đúng ngân sách")
                        .font(.caption)
                        .foregroundStyle(Color("TextSecondary"))
                        .lineLimit(1)
                }

                Spacer()

                // Badge đếm ngược ngày đến Giáng sinh
                HStack(spacing: 6) {
                    Image(systemName: "sparkles")
                        .font(.caption2.weight(.bold))
                        .foregroundStyle(Color("WarmGold"))

                    Text(ChristmasCountdown.countdownText(from: currentDate))
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(Color("PineGreen"))
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(Color("PineGreen").opacity(0.12))
                .clipShape(Capsule())
                .overlay(
                    Capsule()
                        .strokeBorder(Color("PineGreen").opacity(0.25), lineWidth: 1)
                )
                .accessibilityElement(children: .combine)
                .accessibilityLabel(ChristmasCountdown.countdownText(from: currentDate))
            }
            .padding(.horizontal)
            .padding(.top, 6)
            .padding(.bottom, 6)

            Divider()
                .opacity(0.3)
        }
        .background(Color("AppBackground"))
        .onReceive(timer) { newDate in
            currentDate = newDate
        }
    }
}
