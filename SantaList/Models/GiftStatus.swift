import Foundation
import SwiftUI

/// Trạng thái của món quà theo quy trình chuẩn bị
public enum GiftStatus: Int, CaseIterable, Identifiable, Comparable, Codable {
    case idea = 0      // Ý tưởng
    case ordered = 1   // Đã đặt
    case received = 2  // Đã nhận
    case wrapped = 3   // Đã gói

    public var id: Int { rawValue }

    /// Nhãn hiển thị tiếng Việt
    public var title: String {
        switch self {
        case .idea:
            return "Ý tưởng"
        case .ordered:
            return "Đã đặt"
        case .received:
            return "Đã nhận"
        case .wrapped:
            return "Đã gói"
        }
    }

    /// Tên biểu tượng SF Symbol đại diện
    public var systemImage: String {
        switch self {
        case .idea:
            return "lightbulb"
        case .ordered:
            return "shippingbox"
        case .received:
            return "checkmark.circle"
        case .wrapped:
            return "gift"
        }
    }

    /// Tên màu biểu thị trạng thái
    public var colorName: String {
        switch self {
        case .idea:
            return "WarmGold"
        case .ordered:
            return "WarmGold"
        case .received:
            return "PineGreen"
        case .wrapped:
            return "PineGreen"
        }
    }

    /// Màu SwiftUI tương ứng
    public var tintColor: Color {
        switch self {
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

    /// Kiểm tra trạng thái đã hoàn thành (nhận hoặc gói)
    public var isCompleted: Bool {
        self == .received || self == .wrapped
    }

    /// Kiểm tra trạng thái đã chi tiền (từ đã đặt trở lên)
    public var isSpent: Bool {
        self >= .ordered
    }

    /// Xoay vòng trạng thái kế tiếp: idea -> ordered -> received -> wrapped -> idea
    public func next() -> GiftStatus {
        switch self {
        case .idea:
            return .ordered
        case .ordered:
            return .received
        case .received:
            return .wrapped
        case .wrapped:
            return .idea
        }
    }

    public static func < (lhs: GiftStatus, rhs: GiftStatus) -> Bool {
        lhs.rawValue < rhs.rawValue
    }
}
