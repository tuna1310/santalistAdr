import XCTest
@testable import SantaList

final class FormattersTests: XCTestCase {

    func testVNDCurrencyFormatting() {
        XCTAssertEqual(Formatters.formatVND(1_250_000), "1.250.000 ₫")
        XCTAssertEqual(Formatters.formatVND(0), "0 ₫")
        XCTAssertEqual(Formatters.formatVND(650_000), "650.000 ₫")
        XCTAssertEqual(Formatters.formatVND(-250_000), "-250.000 ₫")
    }

    func testDateFormatting() {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(secondsFromGMT: 0)!

        var components = DateComponents()
        components.year = 2026
        components.month = 12
        components.day = 15
        let date = calendar.date(from: components)!

        XCTAssertEqual(Formatters.dateShort.string(from: date), "15/12/2026")
        XCTAssertEqual(Formatters.dateDayMonth.string(from: date), "15/12")
    }
}
