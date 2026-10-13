import XCTest
@testable import SantaList

final class OverdueLogicTests: XCTestCase {

    func testOverdueCases() {
        let calendar = Calendar.current
        let today = Date()

        guard let yesterday = calendar.date(byAdding: .day, value: -1, to: today),
              let tomorrow = calendar.date(byAdding: .day, value: 1, to: today) else {
            XCTFail("Failed to calculate test dates")
            return
        }

        // 1. Quá hạn và chưa hoàn thành (Ý tưởng / Đã đặt) -> Trễ hạn
        let overdueIdeaGift = Gift(title: "Quà A", price: 100, status: .idea, dueDate: yesterday)
        XCTAssertTrue(overdueIdeaGift.isOverdue(referenceDate: today, calendar: calendar))

        let overdueOrderedGift = Gift(title: "Quà B", price: 100, status: .ordered, dueDate: yesterday)
        XCTAssertTrue(overdueOrderedGift.isOverdue(referenceDate: today, calendar: calendar))

        // 2. Hạn hôm qua nhưng đã nhận / đã gói -> KHÔNG trễ hạn
        let completedReceivedGift = Gift(title: "Quà C", price: 100, status: .received, dueDate: yesterday)
        XCTAssertFalse(completedReceivedGift.isOverdue(referenceDate: today, calendar: calendar))

        let completedWrappedGift = Gift(title: "Quà D", price: 100, status: .wrapped, dueDate: yesterday)
        XCTAssertFalse(completedWrappedGift.isOverdue(referenceDate: today, calendar: calendar))

        // 3. Hạn ngày mai -> KHÔNG trễ hạn
        let futureGift = Gift(title: "Quà E", price: 100, status: .idea, dueDate: tomorrow)
        XCTAssertFalse(futureGift.isOverdue(referenceDate: today, calendar: calendar))

        // 4. Không có hạn -> KHÔNG trễ hạn
        let noDueDateGift = Gift(title: "Quà F", price: 100, status: .idea, dueDate: nil)
        XCTAssertFalse(noDueDateGift.isOverdue(referenceDate: today, calendar: calendar))
    }
}
