<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ errorMsg }}
    </div>

    <!-- NAV TABS -->
    <ul class="nav nav-tabs mb-3" id="accountTab" role="tablist">
      <li class="nav-item">
        <button
          id="formTabBtn"
          class="nav-link active"
          data-bs-toggle="tab"
          data-bs-target="#formTab"
          type="button"
          @click="clear"
        >
          Biểu mẫu
        </button>
      </li>
      <li class="nav-item">
        <button
          id="listTabBtn"
          class="nav-link"
          data-bs-toggle="tab"
          data-bs-target="#listTab"
          type="button"
        >
          Danh sách
        </button>
      </li>
    </ul>

    <!-- TAB CONTENT -->
    <div class="tab-content">

      <!-- FORM TAB -->
      <div class="tab-pane fade show active" id="formTab">
        <div class="card shadow-sm">
          <div class="card-body">

            <div class="mb-3">
              <div v-if="previewImage" class="mt-2 d-flex justify-content-center">
                <img :src="previewImage" style="width: 120px; height: 120px; object-fit: cover;" />
              </div>
            </div>

            <div class="mb-3">
              <label>Photo</label>
              <input type="file" class="form-control" ref="fileInput" @change="handleFileUpload" />
            </div>

            <div class="mb-3">
              <input v-model="accountInput.username" class="form-control" :readonly="username" placeholder="Tên đăng nhập"/>
            </div>

            <div class="mb-3">
              <input v-model="accountInput.password" class="form-control" placeholder="Mật khẩu"/>
            </div>

            <div class="mb-3">
              <input v-model="accountInput.fullname" class="form-control" placeholder="Họ và tên"/>
            </div>

            <div class="mb-3">
              <input v-model="accountInput.email" class="form-control" placeholder="Email"/>
            </div>

            <div class="mb-3">
              <input v-model="accountInput.phone" class="form-control" placeholder="Điện thoại"/>
            </div>

            <div class="mb-3">
              <input v-model="accountInput.address" class="form-control" placeholder="Địa chỉ"/>
            </div>

            <div class="form-check mb-3">
              <input id="accountStatusInput" type="checkbox" v-model="accountInput.activated" class="form-check-input">
              <label for="accountStatusInput" class="form-check-label">Còn hoạt động</label>
            </div>

            <!-- role checkbox -->
            <div class="mb-3">
              <label>Quyền</label>
              <div class="d-flex flex-wrap gap-3">
                <div class="form-check" v-for="r in roles" :key="r.name">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    :id="r.name"
                    :value="r"
                    v-model="accountInput.roles"
                  />
                  <label class="form-check-label" :for="r.name">
                    {{ r.fullname }}
                  </label>
                </div>
              </div>
            </div>

            <div class="d-flex justify-content-center gap-3">
              <button class="btn btn-primary" @click="create" :disabled="username">Thêm</button>
              <button class="btn btn-info" @click="update" :disabled="!username">Sửa</button>
              <button class="btn btn-danger" @click="remove" :disabled="!username">Xoá</button>
              <button class="btn btn-secondary" @click="clear">Mới</button>
            </div>

          </div>
        </div>
      </div>

      <!-- TABLE TAB -->
      <div class="tab-pane fade" id="listTab">
        <!-- Filter Form -->
        <div class="row g-2 mb-4">
          <div class="col-12 col-md-3">
            <input v-model="filter.username" type="text" class="form-control" placeholder="Tên đăng nhập" />
          </div>
          <div class="col-12 col-md-3">
            <input v-model="filter.fullname" type="text" class="form-control" placeholder="Họ và tên" />
          </div>
          <div class="col-12 col-md-3">
            <input v-model="filter.email" type="text" class="form-control" placeholder="Email" />
          </div>
          <div class="col-6 col-md-2">
            <select v-model="filter.activated" class="form-select">
              <option :value="null">Trạng thái</option>
              <option value="true">Hoạt động</option>
              <option value="false">Đình chỉ</option>
            </select>
          </div>
          <div class="col-6 col-md-2">
            <select v-model="filter.sortOrderByCreateDate" class="form-select">
              <option :value="null">Ngày tạo</option>
              <option value="ASC">Thấp → Cao</option>
              <option value="DESC">Cao → Thấp</option>
            </select>
          </div>
          <div class="col-12 col-md-1">
            <button class="btn btn-primary w-100" @click="refresh">Làm mới</button>
          </div>
        </div>

        <div class="card shadow-sm">
          <div class="card-body table-responsive">

            <table class="table table-bordered table-hover text-center align-middle">
              <thead class="table-light">
                <tr>
                  <th>#</th>
                  <th>Hình</th>
                  <th>Tên đăng nhập</th>
                  <th>Họ và tên</th>
                  <th>Email</th>
                  <th>Điện thoại</th>
                  <th>Địa chỉ</th>
                  <th>Ngày tạo</th>
                  <th>Quyền</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="(a, index) in accounts" :key="a.username">
                  <td>{{ index + 1 }}</td>
                  <td>
                    <img :src="getImageUrl(a.photo)" style="width: 60px; height: 60px; object-fit: cover;" />
                  </td>
                  <td>{{ a.username }}</td>
                  <td>{{ a.fullname }}</td>
                  <td>{{ a.email }}</td>
                  <td>{{ a.phone }}</td>
                  <td>{{ a.address }}</td>
                  <td>{{ formatDate(a.createDate) }}</td>
                  <td>
                    <span v-for="r in a.roleResponses" :key="r.name" class="badge bg-info me-1">
                      {{ r.fullname }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-warning me-2" @click="edit(a)">
                      Chọn
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <!-- PAGINATION -->
            <nav>
              <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: filter.page === 1 }">
                  <button class="page-link" @click="changePage(1)">&laquo; First</button>
                </li>
                <li
                  v-for="p in totalAccountPages <= 7 ? totalAccountPages : Array.from({length: 7}, (_, i) => Math.min(Math.max(filter.page - 3, 1), totalAccountPages - 6) + i).filter(p => p >= 1 && p <= totalAccountPages)"
                  :key="p"
                  class="page-item"
                  :class="{ active: p === filter.page }"
                >
                  <button class="page-link" @click="changePage(p)">{{ p }}</button>
                </li>
                <li class="page-item" :class="{ disabled: filter.page === totalAccountPages }">
                  <button class="page-link" @click="changePage(totalAccountPages)">Last &raquo;</button>
                </li>
              </ul>
            </nav>

          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
    import Account from '../models/Account'
    import { reactive, ref, onMounted, watch } from 'vue';
    import { Tab } from 'bootstrap';
    import { createAccount, deleteAccount, getAllPaginatedAndFilteredAccounts, updateAccount, getAccountTotalPages} from '../services/accountService';
    import { getAllRoles } from '../services/roleService';

    const fileInput = ref(null);
    const username = ref(null)
    const accountInput = reactive(new Account());
    const filter = ref({
        username: null,
        fullname: null,
        email: null,
        activated: null,
        sortOrderByCreateDate: null,
        page: 1,
        pageSize: 5
    });
    const accounts = ref([]);
    const totalAccountPages = ref(1);
    const roles = ref([]);
    const previewImage = ref(null);
    const selectedFile = ref(null);
    const errorMsg = ref(null);

    const showError = (e, msg) => {
      errorMsg.value = e.response?.data?.message || msg;
      console.log(e);
      setTimeout(() => errorMsg.value = null, 2000);
    }

    const changeTab = (id) => {
      const tabBtn = document.querySelector("#" + id);
        const tab = new Tab(tabBtn);
        tab.show();
    }

    const featchAccounts = async () => {
        try {
            const response1 = await getAllPaginatedAndFilteredAccounts({...filter.value});
            const response2 = await getAccountTotalPages({...filter.value});
            accounts.value = response1.data;
            totalAccountPages.value = response2.data;
        } catch (e) {
            showError(e, "Lỗi khi tải tài khoảng");
        }
    }

    const featchRoles = async () => {
        try {
            const response = await getAllRoles();
            roles.value = response.data;
        } catch (e) {
            showError(e, "Lỗi khi tải loại quyền tài khoảng");
        }
    }

    const create = async () => {
        try {
            const formData = new FormData();
            if (selectedFile.value) {
                formData.append("file", selectedFile.value)
            }
            formData.append("account", 
                new Blob(
                    [JSON.stringify({ ...accountInput })],
                    { type: "application/json" }
                )
            );            
            await createAccount(formData);
            featchAccounts()
            clear();
            changeTab("listTabBtn");
        } catch (e) {
            showError(e, "Lỗi khi lưu tài khoảng");
        }
    }

    const update = async () => {
      try {
        const formData = new FormData();
        if (selectedFile.value) {
            formData.append("file", selectedFile.value)
        }
        formData.append("account", 
            new Blob(
                [JSON.stringify({ ...accountInput })],
                { type: "application/json" }
            )
        );
        await updateAccount(formData, username.value);
        featchAccounts()
        clear();
        changeTab("listTabBtn");
      } catch (e) {
        showError(e, "Lỗi khi lưu sản phẩm");
      }
    }

    const remove = async () => {
        try {
            await deleteAccount(username.value);
            featchAccounts();
            clear();
            changeTab("listTabBtn");
        } catch (error) {
            showError(e, "Lỗi khi xoá sản phẩm");
        }
    }

    const refresh = async () => {
        filter.value = {
            username: null,
            fullname: null,
            email: null,
            activated: null,
            sortOrderByCreateDate: null,
            page: 1,
            pageSize: 5
        }
        featchAccounts();
    }

    const clear = async () => {
        Object.assign(accountInput, new Account());
        selectedFile.value = null;
        previewImage.value = null;
        username.value = null;
        if (fileInput.value) fileInput.value.value = "";

    }

    const edit = async (a) => {
        Object.assign(accountInput, 
          new Account(
            a.username,
            a.password,
            a.fullname,
            a.email,
            a.photo,
            a.phone,
            a.address,
            a.activated,
            a.roleResponses
          )
        );
        username.value = a.username;
        selectedFile.value = null;
        previewImage.value = getImageUrl(a.photo);
        if (fileInput.value) fileInput.value.value = "";
        changeTab("formTabBtn");
    }

    const changePage = (page) => {
        if (page < 1 || page > totalAccountPages.value) return;
        filter.value.page = page;
        featchAccounts();
    }

    watch(filter, () => {
        if (filter.value.page > totalAccountPages.value) {
            filter.value.page = 1
        }
        featchAccounts()
    }, {deep: true});

    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        selectedFile.value = file;
        previewImage.value = URL.createObjectURL(file);
    };

    const formatDate = (date) => {
        return new Date(date).toLocaleDateString();
    };

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
            if (!image) return 'https://placehold.co/300x200';
            return `${BASE_URL}/images/${image}`;
    };

    onMounted(() => {
        featchAccounts();
        featchRoles()
    });
</script>