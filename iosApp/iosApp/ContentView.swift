import ComposeApp
import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    var deeplink: String?

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(deeplink: deeplink)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var deeplink: String?

    var body: some View {
        ComposeView(deeplink: deeplink)
            .ignoresSafeArea()
    }
}
