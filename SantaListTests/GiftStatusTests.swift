import XCTest
@testable import SantaList

final class GiftStatusTests: XCTestCase {

    func testNextCycle() {
        XCTAssertEqual(GiftStatus.idea.next(), .ordered)
        XCTAssertEqual(GiftStatus.ordered.next(), .received)
        XCTAssertEqual(GiftStatus.received.next(), .wrapped)
        XCTAssertEqual(GiftStatus.wrapped.next(), .idea)
    }

    func testVietnameseLabels() {
        XCTAssertEqual(GiftStatus.idea.title, "Ý tưởng")
        XCTAssertEqual(GiftStatus.ordered.title, "Đã đặt")
        XCTAssertEqual(GiftStatus.received.title, "Đã nhận")
        XCTAssertEqual(GiftStatus.wrapped.title, "Đã gói")
    }

    func testStatusComparisons() {
        XCTAssertTrue(GiftStatus.idea < GiftStatus.ordered)
        XCTAssertTrue(GiftStatus.ordered < GiftStatus.received)
        XCTAssertTrue(GiftStatus.received < GiftStatus.wrapped)
    }

    func testStatusFlags() {
        XCTAssertFalse(GiftStatus.idea.isSpent)
        XCTAssertTrue(GiftStatus.ordered.isSpent)
        XCTAssertTrue(GiftStatus.received.isSpent)
        XCTAssertTrue(GiftStatus.wrapped.isSpent)

        XCTAssertFalse(GiftStatus.idea.isCompleted)
        XCTAssertFalse(GiftStatus.ordered.isCompleted)
        XCTAssertTrue(GiftStatus.received.isCompleted)
        XCTAssertTrue(GiftStatus.wrapped.isCompleted)
    }
}
