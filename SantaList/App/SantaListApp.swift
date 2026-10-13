import SwiftUI
import SwiftData

@main
struct SantaListApp: App {
    let container: ModelContainer

    init() {
        do {
            let schema = Schema([
                Person.self,
                Gift.self
            ])
            let modelConfiguration = ModelConfiguration(
                schema: schema,
                isStoredInMemoryOnly: false
            )
            container = try ModelContainer(for: schema, configurations: [modelConfiguration])

            // Nạp dữ liệu mẫu cho lần chạy đầu tiên
            SeedData.seedInitialDataIfNeeded(context: container.mainContext)
        } catch {
            fatalError("Could not initialize ModelContainer: \(error.localizedDescription)")
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onAppear {
                    // Xin quyền thông báo nhắc lịch lần đầu mở app
                    ReminderService.shared.requestAuthorization()
                }
        }
        .modelContainer(container)
    }
}
