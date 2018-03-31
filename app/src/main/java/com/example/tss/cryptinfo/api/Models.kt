package com.example.tss.cryptinfo.api

/**
 * Created by Taha on 3/31/2018.
 */

class Coin {
    var symbol: String? = null
    var coinName: String? = null
    var url: String? = null
    var imageUrl: String? = null
    var algorithm: String? = null
    var proofType: String? = null
    var totalSupply: Long = 0
    var sponsor: Int = 0

    var supply: Double = 0.toDouble()
    var price: Double = 0.toDouble()
    var mktcap: Double = 0.toDouble()
    var vol24h: Double = 0.toDouble()
    var vol24h2: Double = 0.toDouble()
    var open24h: Double = 0.toDouble()
    var high24h: Double = 0.toDouble()
    var low24h: Double = 0.toDouble()
    var trend: Double = 0.toDouble()
    var change: Double = 0.toDouble()

    var histo: String? = null

    var news: String? = null
    var update: Long = 0

    constructor() {}

    constructor(symbol: String) {
        this.symbol = symbol
    }

}

class History {
    var time: Double = 0.toDouble()
    var close: Double = 0.toDouble()
    var high: Double = 0.toDouble()
    var low: Double = 0.toDouble()
    var open: Double = 0.toDouble()
    var volumefrom: Double = 0.toDouble()
    var volumeto: Double = 0.toDouble()
}

class News {
    var newsImageSrc: String? = null

    var newsTitle: String? = null

    var newsDescription: String? = null

    var newsTime: String? = null
        get() = if (field == null || field!!.length < 19) {
            field
        } else {
            field!!.subSequence(0, 19).toString()
        }

    var newsSource: String? = null

    var newsUrl: String? = null

    constructor(newsImageSrc: String, newsTitle: String, newsDescription: String,
                newsTime: String, newsSource: String, newsUrl: String) {
        this.newsImageSrc = newsImageSrc
        this.newsTitle = newsTitle
        this.newsDescription = newsDescription
        this.newsTime = newsTime
        this.newsSource = newsSource
        this.newsUrl = newsUrl
    }
}
