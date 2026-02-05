import SwiftUI

@main
struct iOSApp: App {
    @State private var deeplink: String?

    var body: some Scene {
        WindowGroup {
            ContentView(deeplink: deeplink)
                .onOpenURL { url in
                    deeplink = url.absoluteString
                }
        }
    }
}
