import Foundation
import SwiftData

@Model
public final class Person {
    public var id: UUID = UUID()
    public var name: String = ""
    public var budget: Int = 0
    public var note: String = ""

    @Relationship(deleteRule: .cascade, inverse: \Gift.person)
    public var gifts: [Gift] = []

    public init(id: UUID = UUID(), name: String, budget: Int = 0, note: String = "") {
        self.id = id
        self.name = name
        self.budget = budget
        self.note = note
        self.gifts = []
    }

    /// Tổng chi phí dự kiến cho toàn bộ quà của người này
    public var plannedAmount: Int {
        gifts.reduce(0) { $0 + $1.price }
    }

    /// Tổng chi phí đã chi thực tế (quà ở trạng thái Đã đặt trở lên)
    public var spentAmount: Int {
        gifts.filter { $0.status.isSpent }.reduce(0) { $0 + $1.price }
    }

    /// Số tiền còn lại trong ngân sách (Ngân sách - Dự kiến)
    public var remainingAmount: Int {
        budget - plannedAmount
    }

    /// Kiểm tra người nhận này có đang bị vượt ngân sách không
    public var isOverBudget: Bool {
        plannedAmount > budget
    }

    /// Tỷ lệ dự kiến trên ngân sách (0.0 đến 1.0+)
    public var budgetUsageRatio: Double {
        guard budget > 0 else { return plannedAmount > 0 ? 1.0 : 0.0 }
        return Double(plannedAmount) / Double(budget)
    }
}
