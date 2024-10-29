package com.example.polinelapeduli;

public class Donasi {
    private int id; // Tambahkan properti id
    private String nama;
    private String deskripsi;
    private String kategori;
    private int target;
    private String gambar;

    // Constructor
    public Donasi(int id, String nama, String deskripsi, String kategori, int target, String gambar) {
        this.id = id; // Inisialisasi id
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.target = target;
        this.gambar = gambar;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public int getTarget() {
        return target;
    }

    public String getGambar() {
        return gambar;
    }
}
