import XCTest
import SwiftData
@testable import SantaList

@MainActor
final class SwiftDataCascadeTests: XCTestCase {

    func testCascadeDeletePersonDeletesGifts() throws {
        let schema = Schema([
            Person.self,
            Gift.self
        ])
        let modelConfiguration = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        let container = try ModelContainer(for: schema, configurations: [modelConfiguration])
        let context = container.mainContext

        // 1. Tạo người nhận và 2 món quà
        let person = Person(name: "Lan", budget: 1_000_000)
        let gift1 = Gift(title: "Son dưỡng", price: 250_000, person: person)
        let gift2 = Gift(title: "Nến thơm", price: 350_000, person: person)

        person.gifts.append(gift1)
        person.gifts.append(gift2)

        context.insert(person)
        context.insert(gift1)
        context.insert(gift2)
        try context.save()

        // Kiểm tra đã lưu thành công
        let peopleBefore = try context.fetch(FetchDescriptor<Person>())
        let giftsBefore = try context.fetch(FetchDescriptor<Gift>())
        XCTAssertEqual(peopleBefore.count, 1)
        XCTAssertEqual(giftsBefore.count, 2)

        // 2. Xóa người nhận
        context.delete(person)
        try context.save()

        // 3. Kiểm tra cascade delete: toàn bộ quà của người này cũng phải bị xóa
        let peopleAfter = try context.fetch(FetchDescriptor<Person>())
        let giftsAfter = try context.fetch(FetchDescriptor<Gift>())
        XCTAssertEqual(peopleAfter.count, 0)
        XCTAssertEqual(giftsAfter.count, 0)
    }
}
