import XCTest
import SwiftData
@testable import SantaList

final class BudgetCalculatorTests: XCTestCase {

    func testBudgetCalculations() {
        let person1 = Person(name: "A", budget: 1_000_000)
        let person2 = Person(name: "B", budget: 2_000_000)

        let gift1 = Gift(title: "G1", price: 300_000, status: .idea, person: person1)
        let gift2 = Gift(title: "G2", price: 400_000, status: .ordered, person: person1)
        let gift3 = Gift(title: "G3", price: 500_000, status: .received, person: person2)
        let gift4 = Gift(title: "G4", price: 600_000, status: .wrapped, person: person2)

        person1.gifts = [gift1, gift2]
        person2.gifts = [gift3, gift4]

        let people = [person1, person2]
        let gifts = [gift1, gift2, gift3, gift4]

        // Tổng ngân sách = 1M + 2M = 3.000.000
        XCTAssertEqual(BudgetCalculator.totalBudget(people: people), 3_000_000)

        // Tổng dự kiến = 300k + 400k + 500k + 600k = 1.800.000
        XCTAssertEqual(BudgetCalculator.totalPlanned(gifts: gifts), 1_800_000)

        // Tổng đã chi (status >= .ordered): gift2 (400k) + gift3 (500k) + gift4 (600k) = 1.500.000
        XCTAssertEqual(BudgetCalculator.totalSpent(gifts: gifts), 1_500_000)

        // Tổng còn lại = 3M - 1.8M = 1.200.000
        XCTAssertEqual(BudgetCalculator.totalRemaining(people: people, gifts: gifts), 1_200_000)
        XCTAssertFalse(BudgetCalculator.isOverBudget(people: people, gifts: gifts))
    }

    func testOverBudgetDetection() {
        let person = Person(name: "C", budget: 500_000)
        let expensiveGift = Gift(title: "Phone", price: 800_000, status: .idea, person: person)
        person.gifts = [expensiveGift]

        XCTAssertEqual(BudgetCalculator.totalRemaining(people: [person], gifts: [expensiveGift]), -300_000)
        XCTAssertTrue(BudgetCalculator.isOverBudget(people: [person], gifts: [expensiveGift]))
        XCTAssertTrue(BudgetCalculator.isPersonOverBudget(person: person))
    }
}
