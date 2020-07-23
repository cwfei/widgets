//
//  MarketListWidget.swift
//  MarketListWidget
//
//  Created by cw fei on 21/07/2020.
//

import WidgetKit
import SwiftUI

struct Provider: TimelineProvider {
  public typealias Entry = SimpleEntry

  public func snapshot(with context: Context, completion: @escaping (SimpleEntry) -> ()) {
    let entry = SimpleEntry(date: Date(), coins: [])
    completion(entry)
  }

  func loadCoins(completion: @escaping ([Coin]?, Error?) -> Void) {
    var request = URLRequest.init(url: URL(string: "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false&price_change_percentage=24h")!)
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")

    let dataTask = URLSession.shared.dataTask(with: request) { data, response, error in
      if let error = error {
        completion(nil, error)
        return
      }

      guard let httpURLResponse = (response as? HTTPURLResponse),
        (200...299).contains(httpURLResponse.statusCode) else {
          completion(nil, error)
          return
      }

      guard let data = data else {
        completion(nil, error)
        return
      }

      do {
        let coins = try JSONDecoder().decode([Coin].self, from: data)
        completion(coins, error)
      } catch {
        completion(nil, error)
      }
    }
    dataTask.resume()
  }

  public func timeline(with context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
    loadCoins { (coins, error) in
      let timeline = Timeline(entries: [SimpleEntry(date: Date(), coins: coins)], policy: .atEnd)
      completion(timeline)
    }
  }
}

struct SimpleEntry: TimelineEntry {
  public let date: Date
  let coins: [Coin]?
}

struct PlaceholderView: View {
  var body: some View {
    Text("Placeholder View")
  }
}

struct MarketListWidgetEntryView: View {
  var entry: Provider.Entry

  var body: some View {
    VStack {
      ForEach(entry.coins!) { coin in
        MarketCoinView(coin: coin)
      }
    }
  }
}

@main
struct MarketListWidget: Widget {
  private let kind: String = "MarketListWidget"

  public var body: some WidgetConfiguration {
    StaticConfiguration(kind: kind, provider: Provider(), placeholder: PlaceholderView()) { entry in
      MarketListWidgetEntryView(entry: entry)
    }
      .configurationDisplayName("My Widget")
      .description("This is an example widget.")
      .supportedFamilies([.systemLarge])
  }
}

struct MarketListWidget_Previews: PreviewProvider {
  static var previews: some View {
    MarketListWidgetEntryView(entry: SimpleEntry(date: Date(), coins: []))
      .previewContext(WidgetPreviewContext(family: .systemSmall))
  }
}

struct MarketCoinView: View {
  let coin: Coin

  var body: some View {
    HStack {
      Text("\(coin.id)")
    }
  }
}

struct MarketCoinView_Previews: PreviewProvider {
  static var previews: some View {
    MarketCoinView(coin: Coin(id: "test"))
  }
}
