import Foundation
import SwiftData

@Model
public final class Gift {
    public var id: UUID = UUID()
    public var title: String = ""
    public var price: Int = 0
    public var statusRawValue: Int = 0
    public var dueDate: Date? = nil
    public var occasionRawValue: String = Occasion.christmas.rawValue
    public var note: String = ""
    public var url: String = ""

    public var person: Person? = nil

    public init(
        id: UUID = UUID(),
        title: String,
        price: Int = 0,
        status: GiftStatus = .idea,
        dueDate: Date? = nil,
        occasion: Occasion = .christmas,
        note: String = "",
        url: String = "",
        person: Person? = nil
    ) {
        self.id = id
        self.title = title
        self.price = price
        self.statusRawValue = status.rawValue
        self.dueDate = dueDate
        self.occasionRawValue = occasion.rawValue
        self.note = note
        self.url = url
        self.person = person
    }

    /// Trạng thái quà
    public var status: GiftStatus {
        get {
            GiftStatus(rawValue: statusRawValue) ?? .idea
        }
        set {
            statusRawValue = newValue.rawValue
        }
    }

    /// Dịp tặng quà
    public var occasion: Occasion {
        get {
            Occasion(rawValue: occasionRawValue) ?? .christmas
        }
        set {
            occasionRawValue = newValue.rawValue
        }
    }

    /// Kiểm tra xem món quà có bị trễ hạn hay không
    /// Trễ hạn: có hạn, hạn trước hôm nay và chưa "Đã nhận" (hoặc "Đã gói")
    public func isOverdue(referenceDate: Date = Date(), calendar: Calendar = .current) -> Bool {
        guard let due = dueDate else { return false }
        if status.isCompleted { return false }
        let startOfDue = calendar.startOfDay(for: due)
        let startOfToday = calendar.startOfDay(for: referenceDate)
        return startOfDue < startOfToday
    }

    /// Kiểm tra xem món quà đã được nhận hoặc đã gói chưa
    public var isCompleted: Bool {
        status.isCompleted
    }
}
