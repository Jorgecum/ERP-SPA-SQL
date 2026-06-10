const ROUTES = {
    'dashboard': { title: 'Dashboard', icon: 'bi-pie-chart-fill', render: 'renderDashboard', roles: ['Super Admin', 'Vendedor'] },
    'pos': { title: 'Punto de Venta', icon: 'bi-cash-register', render: 'renderPOS', roles: ['Super Admin', 'Vendedor'] },
    'sales': { title: 'Historial Ventas', icon: 'bi-receipt', render: 'renderVentas', roles: ['Super Admin', 'Vendedor'] },
    'inventory': { title: 'Inventario', icon: 'bi-box-seam', render: 'renderInventario', roles: ['Super Admin'] },
    'purchases': { title: 'Compras', icon: 'bi-cart-check', render: 'renderCompras', roles: ['Super Admin'] },
    'accounting': { title: 'Contabilidad', icon: 'bi-calculator', render: 'renderContabilidad', roles: ['Super Admin'] },
    'installments': { title: 'Cuotas y Pagos', icon: 'bi-journal-check', render: 'renderInstallments', roles: ['Super Admin', 'Vendedor'] },
    'entities': { title: 'Entidades', icon: 'bi-people-fill', render: 'renderEntidades', roles: ['Super Admin', 'Vendedor'] },
    'movements': { title: 'Movimientos Inv.', icon: 'bi-arrow-left-right', render: 'renderMovimientos', roles: ['Super Admin'] },
    'admin': { title: 'Administración', icon: 'bi-gear-fill', render: 'renderAdmin', roles: ['Super Admin'] },
    'reports': { title: 'Reportes', icon: 'bi-bar-chart-line-fill', render: 'renderReports', roles: ['Super Admin'] }
};
function navigate(viewId) {
    if (!ROUTES[viewId] || !ROUTES[viewId].roles.includes(state.user.role)) { 
        Swal.fire({
            icon: 'warning',
            title: 'Acceso Denegado',
            text: 'No tienes permisos para acceder a este módulo.'
        });
        return; 
    }
    state.currentView = viewId;
    if (window.innerWidth < 768) state.sidebarCollapsed = true;
    if (typeof window.renderLayout === 'function') {
        window.renderLayout();
    }
}

window.navigate = navigate;
window.ROUTES = ROUTES;
