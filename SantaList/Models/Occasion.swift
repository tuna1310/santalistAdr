import Foundation
import SwiftUI

/// Dịp tặng quà
public enum Occasion: String, CaseIterable, Identifiable, Codable {
    case christmas = "Giáng sinh"
    case birthday = "Sinh nhật"
    case anniversary = "Kỷ niệm"
    case other = "Khác"

    public var id: String { rawValue }

    public var title: String { rawValue }

    public var systemImage: String {
        switch self {
        case .christmas:
            return "tree.fill"
        case .birthday:
            return "birthday.cake.fill"
        case .anniversary:
            return "heart.fill"
        case .other:
            return "sparkles"
        }
    }
}
