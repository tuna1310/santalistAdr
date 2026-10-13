import Foundation

/// Bộ đếm ngược ngày đến Giáng sinh (25/12)
public struct ChristmasCountdown {
    /// Tính ngày Giáng sinh mục tiêu tiếp theo
    public static func nextChristmas(from referenceDate: Date = Date(), calendar: Calendar = .current) -> Date {
        let currentYear = calendar.component(.year, from: referenceDate)

        var components = DateComponents()
        components.year = currentYear
        components.month = 12
        components.day = 25
        components.hour = 0
        components.minute = 0
        components.second = 0

        guard let targetThisYear = calendar.date(from: components) else {
            return referenceDate
        }

        let startOfToday = calendar.startOfDay(for: referenceDate)
        let startOfTargetThisYear = calendar.startOfDay(for: targetThisYear)

        if startOfToday > startOfTargetThisYear {
            // Đã qua 25/12 năm nay -> đếm đến 25/12 năm sau
            components.year = currentYear + 1
            return calendar.date(from: components) ?? targetThisYear
        }

        return targetThisYear
    }

    /// Số ngày còn lại đến Giáng sinh mục tiêu
    public static func daysRemaining(from referenceDate: Date = Date(), calendar: Calendar = .current) -> Int {
        let target = nextChristmas(from: referenceDate, calendar: calendar)
        let startOfToday = calendar.startOfDay(for: referenceDate)
        let startOfTarget = calendar.startOfDay(for: target)

        let diff = calendar.dateComponents([.day], from: startOfToday, to: startOfTarget)
        return max(0, diff.day ?? 0)
    }

    /// Chuỗi thông báo đếm ngược thân thiện
    public static func countdownText(from referenceDate: Date = Date(), calendar: Calendar = .current) -> String {
        let days = daysRemaining(from: referenceDate, calendar: calendar)
        if days == 0 {
            return "Hôm nay là Giáng sinh! 🎅"
        } else if days == 1 {
            return "Còn 1 ngày đến Giáng sinh 🎄"
        } else {
            return "Còn \(days) ngày đến Giáng sinh 🎄"
        }
    }
}
