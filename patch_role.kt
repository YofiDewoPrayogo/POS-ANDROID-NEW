import com.yofidewo.pos.data.RoleEntity

val roleAdminId = roleDao.insertRole(RoleEntity(name = "Administrator", canEdit = true, canDelete = true, canCreate = true, canManageProducts = true, canViewReports = true, canManageStock = true, canReturnSales = true))
val roleKasirId = roleDao.insertRole(RoleEntity(name = "Kasir", canEdit = false, canDelete = false, canCreate = true, canManageProducts = false, canViewReports = false, canManageStock = false, canReturnSales = false))
