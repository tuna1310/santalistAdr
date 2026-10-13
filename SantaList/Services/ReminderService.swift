import Foundation
import UserNotifications

/// Dịch vụ quản lý thông báo nhắc lịch quà tặng bằng UserNotifications
public final class ReminderService {
    public static let shared = ReminderService()

    private let notificationCenter = UNUserNotificationCenter.current()

    private init() {}

    /// Yêu cầu người dùng cấp quyền thông báo khi mở ứng dụng lần đầu
    public func requestAuthorization() {
        notificationCenter.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Notification permission error: \(error.localizedDescription)")
            }
        }
    }

    /// Đặt lịch thông báo lúc 9:00 sáng trước hạn 1 ngày cho quà chưa nhận/gói
    public func scheduleReminder(for gift: Gift) {
        let identifier = notificationIdentifier(for: gift.id)

        // Luôn hủy thông báo cũ trước khi tính toán đặt lại
        cancelReminder(for: gift.id)

        // Không nhắc nếu không có hạn hoặc quà đã hoàn thành (đã nhận / đã gói)
        guard let dueDate = gift.dueDate, !gift.status.isCompleted else {
            return
        }

        let calendar = Calendar.current

        // Tính ngày trước hạn 1 ngày
        guard let dayBefore = calendar.date(byAdding: .day, value: -1, to: dueDate) else {
            return
        }

        // Đặt giờ thông báo vào lúc 9:00 sáng
        var triggerComponents = calendar.dateComponents([.year, .month, .day], from: dayBefore)
        triggerComponents.hour = 9
        triggerComponents.minute = 0
        triggerComponents.second = 0

        guard let fireDate = calendar.date(from: triggerComponents) else {
            return
        }

        // Nếu thời điểm thông báo đã qua trong quá khứ thì không lên lịch
        if fireDate <= Date() {
            return
        }

        let content = UNMutableNotificationContent()
        content.title = "Nhắc nhở quà tặng: \(gift.title)"
        if let recipientName = gift.person?.name, !recipientName.isEmpty {
            content.body = "Ngày mai là hạn chuẩn bị quà cho \(recipientName) (\(Formatters.formatVND(gift.price)))."
        } else {
            content.body = "Ngày mai là hạn chuẩn bị quà (\(Formatters.formatVND(gift.price)))."
        }
        content.sound = .default

        let trigger = UNCalendarNotificationTrigger(dateMatching: triggerComponents, repeats: false)
        let request = UNNotificationRequest(identifier: identifier, content: content, trigger: trigger)

        notificationCenter.add(request) { error in
            if let error = error {
                print("Failed to schedule notification: \(error.localizedDescription)")
            }
        }
    }

    /// Hủy thông báo theo ID của quà
    public func cancelReminder(for giftId: UUID) {
        let identifier = notificationIdentifier(for: giftId)
        notificationCenter.removePendingNotificationRequests(withIdentifiers: [identifier])
        notificationCenter.removeDeliveredNotifications(withIdentifiers: [identifier])
    }

    /// Tạo identifier định danh chuẩn "gift-<uuid>"
    public func notificationIdentifier(for giftId: UUID) -> String {
        "gift-\(giftId.uuidString)"
    }
}
