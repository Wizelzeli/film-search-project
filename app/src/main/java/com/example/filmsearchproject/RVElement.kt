package com.example.filmsearchproject

    data class ElementCore(
        val page: Int,
        val results: List<ElementData>,
        val total_pages: Int,
        val total_results: Int
    )

    data class ElementData(
        val adult: Boolean,
        val backdrop_path: String,
        val genre_ids: List<Int>,
        val id: Int,
        val original_language: String,
        val original_title: String,
        val overview: String,
        val popularity: Double,
        val poster_path: String,
        val release_date: String,
        val title: String,
        val video: Boolean,
        val vote_average: Double,
        val vote_count: Int
    )

    data class RVElement(
        var title: String,
        var description: String,
        var date: String,
        var image: String,
    )
