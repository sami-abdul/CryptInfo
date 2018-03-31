package com.example.tss.cryptinfo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.api.News

import butterknife.BindView
import butterknife.ButterKnife

class NewsAdapter(context: Context, newses: List<News>) : ArrayAdapter<News>(context, 0, newses) {

    @BindView(R.id.news_image)
    internal var imageView: ImageView? = null
    @BindView(R.id.news_title)
    internal var titleView: TextView? = null
    @BindView(R.id.news_description)
    internal var descriptionView: TextView? = null
    @BindView(R.id.news_time)
    internal var timeView: TextView? = null
    @BindView(R.id.news_source)
    internal var sourceView: TextView? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.item_news_list, parent, false)
        }

        ButterKnife.bind(this, listItemView!!)

        val currentNews = getItem(position)

        titleView!!.text = currentNews!!.newsTitle
        descriptionView!!.text = currentNews.newsDescription
        sourceView!!.text = currentNews.newsSource
        timeView!!.text = currentNews.newsTime

        val imageUrl = currentNews.newsImageSrc

        if (imageUrl != null) {
            Glide.with(context)
                    .load(currentNews.newsImageSrc)
                    .into(imageView!!)

            setViewVisibility(imageView, true)
        } else {
            setViewVisibility(imageView, false)
        }

        return listItemView
    }

    private fun setViewVisibility(view: View?, isShown: Boolean) {
        if (isShown) {
            view!!.visibility = View.VISIBLE
        } else {
            view!!.visibility = View.INVISIBLE
        }
    }
}
