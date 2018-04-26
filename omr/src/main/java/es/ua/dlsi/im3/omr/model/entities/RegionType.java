package es.ua.dlsi.im3.omr.model.entities;

public enum RegionType {
    title, author, staff, lyrics, unknwon, marginalia,
    containsStaff, // this separation is rough, it contains the staff, but also lyrics
    all // actually, this is not a region, it is used to encode training sets where we don't have any region marked
}
