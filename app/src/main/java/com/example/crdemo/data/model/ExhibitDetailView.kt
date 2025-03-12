package com.example.crdemo.data.model

import androidx.room.DatabaseView

@DatabaseView(
    """
    SELECT 
        e.e_name AS exhibitName,
        (SELECT GROUP_CONCAT(a.nameChinese, '; ') 
         FROM animals a 
         WHERE a.location LIKE '%' || e.e_name || '%') AS animalNames,
        (SELECT GROUP_CONCAT(p.nameChinese, '; ') 
         FROM plants p 
         WHERE p.brief LIKE '%' || e.e_name || '%') AS plantNames
    FROM exhibits e
    """
)
data class ExhibitDetailView(
    val exhibitName: String,
    val animalNames: String?,
    val plantNames: String?
)
