<template>
<Navbar></Navbar>
<router-view></router-view>
<Footer></Footer>
</template>

<script setup>
    import Navbar from './components/Navbar.vue';
    import Footer from './components/Footer.vue';
</script>

<!--
    A [something] basis(Collocation): vòng tuần hoàn, Example: A regular basis, A daily baisis
    Basic(Ajd): mang tính cơ bảng

    BASIC VUE JS
    1. javascript run first then it comes to html
    2. setup tag allows vue to know to where the the script of its template
    with out setup, the script will be lost
-->
