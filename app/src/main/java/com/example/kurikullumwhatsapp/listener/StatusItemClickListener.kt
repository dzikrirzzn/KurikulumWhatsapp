package com.example.kurikullumwhatsapp.listener

import com.example.kurikullumwhatsapp.model.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)
}