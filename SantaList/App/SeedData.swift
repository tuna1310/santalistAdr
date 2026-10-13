import Foundation
import SwiftData

/// Bộ khởi tạo dữ liệu mẫu lần chạy đầu tiên
public struct SeedData {
    public static let seededKey = "hasSeededInitialData"

    public static func seedInitialDataIfNeeded(context: ModelContext) {
        // Kiểm tra cờ trong UserDefaults
        if UserDefaults.standard.bool(forKey: seededKey) {
            return
        }

        // Kiểm tra xem đã có dữ liệu nào trong database chưa
        let descriptor = FetchDescriptor<Person>()
        if let existingCount = try? context.fetchCount(descriptor), existingCount > 0 {
            UserDefaults.standard.set(true, forKey: seededKey)
            return
        }

        let calendar = Calendar.current
        let currentYear = calendar.component(.year, from: Date())

        // 1. Tạo 2 người nhận mẫu
        let mom = Person(
            name: "Mẹ",
            budget: 2_000_000,
            note: "Người phụ nữ tuyệt vời nhất gia đình"
        )

        let minh = Person(
            name: "Minh (bạn thân)",
            budget: 800_000,
            note: "Bạn thân từ thời đại học"
        )

        context.insert(mom)
        context.insert(minh)

        // Tạo ngày hạn mẫu cho năm hiện tại
        let dateDec15 = calendar.date(from: DateComponents(year: currentYear, month: 12, day: 15, hour: 12))
        let dateDec20 = calendar.date(from: DateComponents(year: currentYear, month: 12, day: 20, hour: 12))
        let dateDec10 = calendar.date(from: DateComponents(year: currentYear, month: 12, day: 10, hour: 12))

        // 2. Tạo 3 món quà mẫu
        let gift1 = Gift(
            title: "Khăn len cashmere",
            price: 650_000,
            status: .ordered,
            dueDate: dateDec15,
            occasion: .christmas,
            note: "Màu kem nhã nhặn, chất len mềm ấm",
            url: "https://example.com/khan-len",
            person: mom
        )

        let gift2 = Gift(
            title: "Bộ trà thảo mộc",
            price: 300_000,
            status: .idea,
            dueDate: dateDec20,
            occasion: .christmas,
            note: "Trà hoa cúc và bạc hà thư giãn",
            url: "",
            person: mom
        )

        let gift3 = Gift(
            title: "Sách Atomic Habits",
            price: 220_000,
            status: .wrapped,
            dueDate: dateDec10,
            occasion: .christmas,
            note: "Bản bìa cứng tiếng Việt tái bản",
            url: "https://example.com/atomic-habits",
            person: minh
        )

        mom.gifts.append(gift1)
        mom.gifts.append(gift2)
        minh.gifts.append(gift3)

        context.insert(gift1)
        context.insert(gift2)
        context.insert(gift3)

        try? context.save()

        // Lên lịch nhắc nhở
        ReminderService.shared.scheduleReminder(for: gift1)
        ReminderService.shared.scheduleReminder(for: gift2)
        ReminderService.shared.scheduleReminder(for: gift3)

        UserDefaults.standard.set(true, forKey: seededKey)
    }
}
