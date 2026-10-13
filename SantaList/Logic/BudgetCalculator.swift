import Foundation

/// Bộ tính toán ngân sách tập trung cho ứng dụng SantaList
public struct BudgetCalculator {
    /// Tổng ngân sách = tổng ngân sách mọi người nhận
    public static func totalBudget(people: [Person]) -> Int {
        people.reduce(0) { $0 + max(0, $1.budget) }
    }

    /// Dự kiến = tổng giá mọi quà
    public static func totalPlanned(gifts: [Gift]) -> Int {
        gifts.reduce(0) { $0 + max(0, $1.price) }
    }

    /// Đã chi = tổng giá quà từ trạng thái "Đã đặt" trở lên
    public static func totalSpent(gifts: [Gift]) -> Int {
        gifts.filter { $0.status.isSpent }.reduce(0) { $0 + max(0, $1.price) }
    }

    /// Còn lại = Ngân sách - Dự kiến (âm khi vượt ngân sách)
    public static func totalRemaining(people: [Person], gifts: [Gift]) -> Int {
        totalBudget(people: people) - totalPlanned(gifts: gifts)
    }

    /// Kiểm tra toàn bộ danh sách có đang vượt ngân sách không
    public static func isOverBudget(people: [Person], gifts: [Gift]) -> Bool {
        totalRemaining(people: people, gifts: gifts) < 0
    }

    /// Tính chi phí dự kiến cho một người cụ thể
    public static func personPlanned(person: Person) -> Int {
        person.gifts.reduce(0) { $0 + max(0, $1.price) }
    }

    /// Tính chi phí đã chi thực tế cho một người cụ thể
    public static func personSpent(person: Person) -> Int {
        person.gifts.filter { $0.status.isSpent }.reduce(0) { $0 + max(0, $1.price) }
    }

    /// Tính số tiền còn lại cho một người cụ thể
    public static func personRemaining(person: Person) -> Int {
        person.budget - personPlanned(person: person)
    }

    /// Kiểm tra người cụ thể có vượt ngân sách không
    public static func isPersonOverBudget(person: Person) -> Bool {
        personPlanned(person: person) > person.budget
    }
}
