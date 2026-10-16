# SantaList: iOS ➔ Android 100% Parity Matrix

## 1. Overview & Single Source of Truth
- **iOS Source Reference**: `santalist_ios` (Swift 5.9+, SwiftUI, SwiftData, Swift Charts, UserNotifications)
- **Target Android**: Kotlin 2.0.21, Jetpack Compose, Material 3, Room 2.6.1, ViewModel + StateFlow, Navigation Compose, WorkManager / AlarmManager + Notifications.
- **Language**: English verbatim across the entire app. Default `strings.xml`.
- **Package / Application ID**: `com.santalist.app`, App Name: `SantaList`.
- **Target SDK**: minSdk 26, targetSdk 35, compileSdk 35.

---

## 2. Design System & Theme Tokens

| Token | Light Mode Hex | Dark Mode Hex | Usage |
| :--- | :---: | :---: | :--- |
| **PineGreen** | `#0F4A35` | `#4FB98A` | Primary accent, title brand, active tab, completed badges |
| **CranberryRed** | `#B3243B` | `#EE6A80` | Overdue indicator, over-budget warning, destructive actions |
| **WarmGold** | `#C8912B` | `#E0AE4D` | Countdown badge, planned amounts, chart planned bars, idea status |
| **StatusOrange** | `#F97316` | `#FB923C` | Ordered status badge & spent KPI metric |
| **AppBackground** | `#F3F6F4` | `#0E1A15` | Window / Scaffold background |
| **AppCard** | `#FFFFFF` | `#16251E` | Card surfaces, sheet background, dialog background |
| **TextPrimary** | `#111827` | `#F9FAFB` | Headings, gift titles, person names, primary values |
| **TextSecondary** | `#4B5563` | `#9CA3AF` | Slogan, subtitles, date labels, notes |

---

## 3. Screen-by-Screen Parity Specifications

### Common Header (`AppHeaderView`)
- **Brand Line**: Santa Emoji 🎅 + "SantaList" (bold rounded headline, PineGreen)
- **Slogan**: "Gifts for the right people, on time, on budget" (caption, TextSecondary)
- **Countdown Badge**: Sparkles icon ✨ + "X days until Christmas 🎄" / "1 day until Christmas 🎄" / "Today is Christmas! 🎅"
  - Rounded capsule, subtle PineGreen background tint, fine border stroke.
  - Rollover logic: If today > Dec 25 of current year, counts down to Dec 25 of next year.

### Tab 1: Recipients (`PeopleView`)
- **Empty State**: `person.crop.circle.badge.plus` icon, "No recipients yet", "Add your loved ones to start tracking gifts and managing budgets.", button "Add First Recipient".
- **Recipient Cards**:
  - Name (headline) + Note (caption)
  - 3-dots Menu: "Edit Recipient", "Delete Recipient"
  - Summary row: Gift icon + "X gifts" | "Planned: $... / $..."
  - `BudgetProgressBar`: green on track, CranberryRed when Planned > Budget. Shows "% of budget", "Over $..." or "Remaining $...".
- **Actions**:
  - Tap card ➔ Recipient Detail (`PersonDetailView`)
  - Toolbar "+" button ➔ Add Recipient Bottom Sheet
  - Floating Action Button "+ Add Gift" (bottom-right capsule)

### Recipient Detail (`PersonDetailView`)
- **Top Summary Card**: Recipient Name, Note, Edit pencil icon, Mini KPI metrics (Budget, Planned, Remaining), BudgetProgressBar.
- **Gift Section**: Headline "Gifts (X)" + "+ Add Gift" button.
- **Gift Card Row**:
  - Occasion circle icon (PineGreen background)
  - Title, Price, Due date ("Today", "Tomorrow", "Yesterday", "dd/MM/yyyy", or "Overdue (dd/MM/yyyy)")
  - `StatusChip`: Idea (WarmGold) ➔ Ordered (Orange) ➔ Received (PineGreen) ➔ Wrapped (PineGreen)
  - Interactive chip: Tap advances to next status, long-press opens menu with all 4 statuses.
  - Swipe to delete: reveal red Trash action with confirmation dialog.
  - Tap card ➔ Edit Gift Bottom Sheet.
  - Top bar delete icon ➔ Delete Recipient dialog (cascade delete warning).

### Tab 2: Timeline (`TimelineView`)
- **Filter Segmented Bar**: "All", "Pending", "Overdue", "Completed"
- **Sorting**: Due date ascending (earliest first), gifts without due date at the end (`dueDate == null`).
- **Gift Timeline Card**:
  - Date Pill Badge:
    - Overdue: Warning icon ⚠️ + "Overdue • dd/MM/yyyy" (CranberryRed) + red outline border on card
    - Completed (Received / Wrapped): Checkmark icon + "Completed • dd/MM/yyyy" (PineGreen)
    - Pending: Calendar icon + "Due: dd/MM/yyyy" (WarmGold)
    - No due date: "No due date"
  - Recipient pill with person icon
  - Occasion icon, Title, Price, Occasion title
  - StatusChip with tap-to-cycle & long-press menu
  - Swipe to delete with confirmation dialog
- **Floating Action Button**: "+ Add Gift" capsule

### Tab 3: Budget (`BudgetView`)
- **4 KPI Cards (2x2 Grid)**:
  - Total Budget (`wallet.pass.fill`, PineGreen) = sum of all recipients' budgets
  - Planned (`list.clipboard.fill`, WarmGold) = sum of all gifts' prices
  - Spent (`creditcard.fill`, Orange) = sum of gifts where status >= Ordered
  - Remaining (`checkmark.circle.fill` / warning, PineGreen / CranberryRed) = Total Budget - Planned
- **Budget Comparison Chart (Canvas Compose)**:
  - Side-by-side bar chart: PineGreen bar (Budget) vs WarmGold bar (Planned) for each recipient
  - Legend on top-right: "Budget" & "Planned"
  - Y-axis formatted in compact currency (e.g. 1M, 500k)
- **Per-Person Breakdown List**:
  - Name, Planned / Budget, BudgetProgressBar turning red when planned > budget

### Bottom Sheets (Add/Edit Forms)
- **Add / Edit Recipient Sheet**:
  - Title: "Add Recipient" / "Edit Recipient"
  - Name: TextField with real-time error "Please enter recipient name"
  - Budget: Number TextField with error "Budget must be a positive number", preview "Equivalent: ..."
  - Notes: Multiline TextField
  - Buttons: "Cancel" & "Save" / "Add"
- **Add / Edit Gift Sheet**:
  - Title: "Add Gift" / "Edit Gift"
  - Recipient dropdown (error: "Please select a recipient")
  - Title: TextField (error: "Please enter gift title")
  - Price: Number TextField (error: "Price must be a non-negative number")
  - Occasion Dropdown: Christmas, Birthday, Anniversary, Other
  - Status Dropdown: Idea, Ordered, Received, Wrapped
  - Due Date Toggle + DatePicker + "Remove Due Date" button
  - Store URL: URL TextField
  - Notes: Multiline TextField
  - Buttons: "Cancel" & "Save" / "Add"

---

## 4. Platform Adaptations (Android Native Parity)

| iOS Concept | Android Equivalent | Implementation Notes |
| :--- | :--- | :--- |
| SwiftUI Sheet | `ModalBottomSheet` (Material 3) | Full drag handle, same form padding, rounded top corners |
| SwiftData `@Model` | Room `@Entity` with ForeignKeys | `ForeignKey.CASCADE` on `personId` so deleting Person cascades to Gifts |
| Swift Charts | Canvas Compose `drawRect` | Side-by-side bars, rounded tops, identical color scale & axes |
| UserNotifications | `AlarmManager` / `WorkManager` + `NotificationManager` | Scheduled at 9:00 AM 1 day prior, `POST_NOTIFICATIONS` runtime permission on Android 13+, `gift-<uuid>` tag |
| TabView | `NavigationBar` / `NavigationBarItem` | 3 tabs, same icons, `PineGreen` indicator & active tint |
| ContextMenu | `DropdownMenu` on long click | Triggered by `combinedClickable(onLongClick = ...)` |
| `swipeActions` | `SwipeToDismissBox` | Swiping left reveals red trash container with confirmation dialog |
| `ContentUnavailableView` | Custom `EmptyStateView` | Same layout: large subtle icon, bold title, body message, prominent button |
