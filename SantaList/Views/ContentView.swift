import SwiftUI
import SwiftData

/// Màn hình chính điều hướng TabView 3 tab: Người nhận | Dòng thời gian | Ngân sách
/// Hỗ trợ cả iPhone và iPad với bố cục thích ứng
struct ContentView: View {
    @State private var selectedTab: Int = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            PeopleView()
                .tabItem {
                    Label("Người nhận", systemImage: "person.2.fill")
                }
                .tag(0)

            TimelineView()
                .tabItem {
                    Label("Dòng thời gian", systemImage: "clock.arrow.circlepath")
                }
                .tag(1)

            BudgetView()
                .tabItem {
                    Label("Ngân sách", systemImage: "chart.pie.fill")
                }
                .tag(2)
        }
        .tint(Color("PineGreen"))
    }
}
