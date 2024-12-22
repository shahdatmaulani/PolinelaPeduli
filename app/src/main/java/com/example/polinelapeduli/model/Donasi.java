package com.example.polinelapeduli.model;

public class Donasi {
    private int id; // ID donasi
    private String nama; // Nama donasi
    private String deskripsi; // Deskripsi donasi
    private String kategori; // Kategori donasi
    private int target; // Target donasi
    private String gambar; // URL gambar donasi
    private String email; // Email donor

    // Constructor
    public Donasi(int id, String nama, String deskripsi, String kategori, int target, String gambar, String email) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.target = target;
        this.gambar = gambar;
        this.email = email; // Inisialisasi email
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

    public String getEmail() {
        return email; // Getter untuk email
    }

    // Setter untuk email (jika diperlukan)
    public void setEmail(String email) {
        this.email = email;
    }
}
