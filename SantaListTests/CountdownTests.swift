import XCTest
@testable import SantaList

final class CountdownTests: XCTestCase {

    func testChristmasCountdownDays() {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(secondsFromGMT: 0)!

        // Trường hợp 1: Ngày 24/12 -> Còn 1 ngày
        var comp24 = DateComponents()
        comp24.year = 2026
        comp24.month = 12
        comp24.day = 24
        let dateDec24 = calendar.date(from: comp24)!
        XCTAssertEqual(ChristmasCountdown.daysRemaining(from: dateDec24, calendar: calendar), 1)
        XCTAssertEqual(ChristmasCountdown.countdownText(from: dateDec24, calendar: calendar), "Còn 1 ngày đến Giáng sinh 🎄")

        // Trường hợp 2: Đúng ngày 25/12 -> 0 ngày
        var comp25 = DateComponents()
        comp25.year = 2026
        comp25.month = 12
        comp25.day = 25
        let dateDec25 = calendar.date(from: comp25)!
        XCTAssertEqual(ChristmasCountdown.daysRemaining(from: dateDec25, calendar: calendar), 0)
        XCTAssertEqual(ChristmasCountdown.countdownText(from: dateDec25, calendar: calendar), "Hôm nay là Giáng sinh! 🎅")

        // Trường hợp 3: Ngày 26/12 -> Đã qua -> Đếm đến 25/12 năm sau (2027)
        var comp26 = DateComponents()
        comp26.year = 2026
        comp26.month = 12
        comp26.day = 26
        let dateDec26 = calendar.date(from: comp26)!
        let remainingFrom26 = ChristmasCountdown.daysRemaining(from: dateDec26, calendar: calendar)
        // 2027 không phải năm nhuận -> 364 ngày từ 26/12/2026 đến 25/12/2027
        XCTAssertEqual(remainingFrom26, 364)

        // Trường hợp 4: Ngày 20/12 -> Còn 5 ngày
        var comp20 = DateComponents()
        comp20.year = 2026
        comp20.month = 12
        comp20.day = 20
        let dateDec20 = calendar.date(from: comp20)!
        XCTAssertEqual(ChristmasCountdown.daysRemaining(from: dateDec20, calendar: calendar), 5)
        XCTAssertEqual(ChristmasCountdown.countdownText(from: dateDec20, calendar: calendar), "Còn 5 ngày đến Giáng sinh 🎄")
    }
}
