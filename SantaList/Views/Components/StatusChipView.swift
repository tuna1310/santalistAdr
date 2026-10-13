import SwiftUI

/// Chip hiển thị trạng thái quà tặng:
/// - Chạm nhẹ để chuyển sang trạng thái kế tiếp (next)
/// - Nhấn giữ (contextMenu) để chọn trực tiếp trạng thái bất kỳ
struct StatusChipView: View {
    let status: GiftStatus
    var onNext: (() -> Void)? = nil
    var onSelect: ((GiftStatus) -> Void)? = nil

    var body: some View {
        Button {
            onNext?()
        } label: {
            HStack(spacing: 5) {
                Image(systemName: status.systemImage)
                    .font(.caption2.weight(.bold))
                Text(status.title)
                    .font(.caption.weight(.semibold))
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 5)
            .background(backgroundColor.opacity(0.15))
            .foregroundStyle(foregroundColor)
            .clipShape(Capsule())
            .overlay(
                Capsule()
                    .strokeBorder(foregroundColor.opacity(0.35), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Trạng thái: \(status.title). Chạm để đổi bước kế tiếp.")
        .contextMenu {
            ForEach(GiftStatus.allCases) { item in
                Button {
                    onSelect?(item)
                } label: {
                    Label(item.title, systemImage: item.systemImage)
                }
            }
        }
    }

    private var backgroundColor: Color {
        switch status {
        case .idea:
            return Color("WarmGold")
        case .ordered:
            return Color.orange
        case .received:
            return Color("PineGreen")
        case .wrapped:
            return Color("PineGreen")
        }
    }

    private var foregroundColor: Color {
        switch status {
        case .idea:
            return Color("WarmGold")
        case .ordered:
            return Color.orange
        case .received:
            return Color("PineGreen")
        case .wrapped:
            return Color("PineGreen")
        }
    }
}
