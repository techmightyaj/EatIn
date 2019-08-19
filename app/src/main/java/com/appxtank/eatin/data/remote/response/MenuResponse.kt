package com.appxtank.eatin.data.remote.response

import com.google.gson.annotations.Expose

data class MenuResponse(
    @Expose var variants: Variants
)

data class Variants(
    @Expose var exclude_list: List<List<ExcludeList>>,
    @Expose var variant_groups: List<VariantGroup>
)

data class VariantGroup(
    @Expose var group_id: String,
    @Expose var name: String,
    @Expose var variations: List<Variation>
)

data class Variation(
    @Expose var default: Int,
    @Expose var id: String,
    @Expose var inStock: Int,
    @Expose var isVeg: Int,
    @Expose var name: String,
    @Expose var price: Int
)

data class ExcludeList(
    @Expose var group_id:String,
    @Expose var variation_id:String
)