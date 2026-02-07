import SwiftUI
import FirebaseCore
import FirebaseFirestore

// For full explanation
// https://firebase.google.com/docs/ios/learn-more?hl=en#swiftui
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        print("Application is starting up. ApplicationDelegate didFinishLaunchingWithOptions.")
        FirebaseApp.configure()
        return true
    }
}

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
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
