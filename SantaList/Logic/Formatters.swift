import Foundation

/// Bộ định dạng tiền tệ và ngày tháng chuẩn theo yêu cầu ứng dụng
public struct Formatters {
    /// Formatter tiền tệ VNĐ chuẩn định dạng "1.250.000 ₫"
    public static let currency: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.locale = Locale(identifier: "vi_VN")
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₫"
        formatter.maximumFractionDigits = 0
        formatter.groupingSeparator = "."
        formatter.decimalSeparator = ","
        formatter.positiveFormat = "#,##0 ¤"
        formatter.negativeFormat = "-#,##0 ¤"
        return formatter
    }()

    /// Chuyển đổi số nguyên sang chuỗi tiền tệ VNĐ (ví dụ: "1.250.000 ₫")
    public static func formatVND(_ amount: Int) -> String {
        if let formatted = currency.string(from: NSNumber(value: amount)) {
            return formatted
        }
        return "\(amount) ₫"
    }

    /// Định dạng ngày ngắn gọn (dd/MM/yyyy)
    public static let dateShort: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "vi_VN")
        formatter.dateFormat = "dd/MM/yyyy"
        return formatter
    }()

    /// Định dạng ngày và tháng (dd/MM)
    public static let dateDayMonth: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "vi_VN")
        formatter.dateFormat = "dd/MM"
        return formatter
    }()

    /// Chuỗi hiển thị thân thiện cho hạn chót
    public static func dueDateString(for date: Date?, calendar: Calendar = .current) -> String {
        guard let date = date else { return "Không có hạn" }
        if calendar.isDateInToday(date) {
            return "Hôm nay"
        } else if calendar.isDateInTomorrow(date) {
            return "Ngày mai"
        } else if calendar.isDateInYesterday(date) {
            return "Hôm qua"
        }
        return dateShort.string(from: date)
    }
}
