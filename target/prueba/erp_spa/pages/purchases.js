async function renderCompras(c) {
    const provs = await api.getEntities(); 
    const providers = provs.filter(p => p.type === 'PROVEEDOR');
    const prods = await api.getProducts();
    const compras = await api.getPurchases();
    const orders = await api.getPurchaseOrders();

    const provHtml = providers.map(p => `<option value="${p.id}">${p.docType} ${p.document} - ${p.name}</option>`).join('');
    const prodHtml = prods.map(p => `<option value="${p.id}" data-price="${p.price}">${p.name}</option>`).join('');
    
    window.comprasContext = { provHtml, prodHtml }; // Save for modal

    const pendingOrders = orders.filter(o => o.status === 'PENDIENTE');
    const ocOpts = pendingOrders.map(o => `<option value="${o.id}">${o.correlative} - ${providers.find(p=>p.id===o.providerId)?.name}</option>`).join('');

    const crRows = compras.map(com => `
        <tr class="hover:bg-[#111827]/40 transition-colors border-b border-[#334155] last:border-0">
            <td class="p-4 whitespace-nowrap font-medium text-[#F8FAFC]">FAC-${com.nroFactura}</td>
            <td class="p-4 whitespace-nowrap text-[#CBD5E1]">${providers.find(p=>p.id===com.providerId)?.name||'N/A'}</td>
            <td class="p-4 whitespace-nowrap text-[#CBD5E1]">${new Date(com.date).toLocaleDateString()}</td>
            <td class="p-4 whitespace-nowrap text-[#F8FAFC] font-bold">${formatMoney(com.total)}</td>
            <td class="p-4 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                    Ingresado
                </span>
            </td>
        </tr>
    `).join('');
    
    const ocRows = orders.map(o => {
        let b = o.status==='PENDIENTE'?'bg-amber-500/20 text-amber-400 border border-amber-500/30':(o.status==='APROBADA'?'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30':'bg-red-500/20 text-red-400 border border-red-500/30');
        let dot = o.status==='PENDIENTE'?'bg-amber-500':(o.status==='APROBADA'?'bg-emerald-500':'bg-red-500');
        return `
            <tr class="hover:bg-[#111827]/40 transition-colors border-b border-[#334155] last:border-0">
                <td class="p-4 whitespace-nowrap font-medium text-[#F8FAFC]">${o.correlative}</td>
                <td class="p-4 whitespace-nowrap text-[#CBD5E1]">${providers.find(p=>p.id===o.providerId)?.name||'N/A'}</td>
                <td class="p-4 whitespace-nowrap text-[#CBD5E1]">${o.date}</td>
                <td class="p-4 whitespace-nowrap text-[#F8FAFC] font-bold">${formatMoney(o.estimatedTotal)}</td>
                <td class="p-4 whitespace-nowrap">
                    <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold ${b}">
                        <span class="w-1.5 h-1.5 rounded-full ${dot}"></span>
                        ${o.status}
                    </span>
                </td>
            </tr>
        `;
    }).join('');

    c.innerHTML = `
        <div class="mb-6 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4" data-aos="fade-down">
            <div>
                <h2 class="text-2xl font-bold text-[#F8FAFC] tracking-tight">Gestión de Compras y Abastecimiento</h2>
                <p class="text-sm text-[#CBD5E1] mt-1">Registra facturas reales de compra o genera cotizaciones/órdenes de compra (OC) para proveedores.</p>
            </div>
            <button class="bg-[#1E293B] border border-[#334155] hover:bg-[#334155] text-[#F8FAFC] px-4 py-2 rounded-xl font-semibold shadow-sm transition-all flex items-center gap-2" onclick="openOCModal()">
                <i class="bi bi-file-earmark-plus"></i> Generar Orden de Compra (OC)
            </button>
        </div>

        <div class="grid grid-cols-1 gap-6 mb-6">
            <div class="bg-[#1E293B] rounded-2xl shadow-sm border border-[#334155] p-6" data-aos="fade-up">
                <h3 class="text-lg font-bold text-[#F8FAFC] mb-4 flex items-center gap-2 border-b border-[#334155] pb-3 text-emerald-400">
                    <i class="bi bi-box-arrow-in-down"></i> Registrar Compra Real (Recepción)
                </h3>
                
                <form id="compra-form" class="space-y-4">
                    <div class="p-4 rounded-xl bg-emerald-500/5 border border-emerald-500/20">
                        <label class="block text-xs font-bold text-emerald-400 uppercase tracking-wider mb-2">Vincular a OC Pendiente (Opcional)</label>
                        <select id="c-order-link" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/20 transition-all py-2.5 outline-none" onchange="linkOC()">
                            <option value="">-- Compra Directa (Sin OC) --</option>
                            ${ocOpts}
                        </select>
                    </div>
                    
                    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div>
                            <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Proveedor</label>
                            <select id="c-prov" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" required>
                                <option value="">Seleccionar...</option>
                                ${provHtml}
                            </select>
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Factura N°</label>
                            <input type="text" id="c-fac" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" required placeholder="001-00021">
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Fecha Factura</label>
                            <input type="date" id="c-date" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" value="${new Date().toISOString().split('T')[0]}" required>
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Moneda</label>
                            <select class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" required>
                                <option>Soles (PEN)</option>
                                <option>Dólares (USD)</option>
                            </select>
                        </div>
                    </div>

                    <div class="p-4 rounded-xl bg-[#111827] border border-[#334155]">
                        <h4 class="text-sm font-bold text-[#F8FAFC] mb-3 uppercase tracking-wider text-slate-400">Agregar Producto</h4>
                        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
                            <div class="md:col-span-2">
                                <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Producto</label>
                                <select id="c-prod" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none">
                                    ${prodHtml}
                                </select>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Cant. Real</label>
                                <input type="number" id="c-cant" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" value="1" min="1">
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-[#CBD5E1] mb-1">Costo U. Real</label>
                                <div class="flex gap-2">
                                    <input type="number" id="c-cost" class="w-full rounded-xl border-[#334155] bg-[#1F2937] text-[#F8FAFC] focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all py-2.5 outline-none" step="0.01">
                                    <button type="button" class="bg-[#1E293B] hover:bg-[#334155] border border-[#334155] text-[#F8FAFC] px-4 py-2.5 rounded-xl font-bold transition-all whitespace-nowrap" onclick="addCompraItem()">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla temporal -->
                    <div class="overflow-x-auto rounded-xl border border-[#334155]">
                        <table class="w-full text-left border-collapse" id="c-table">
                            <thead>
                                <tr class="bg-[#111827] border-b border-[#334155]">
                                    <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase tracking-wider">Producto</th>
                                    <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase tracking-wider">Cantidad Real</th>
                                    <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase tracking-wider">Costo Unitario</th>
                                    <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase tracking-wider">Subtotal</th>
                                    <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase tracking-wider text-right">Acción</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-[#334155]">
                            </tbody>
                        </table>
                    </div>

                    <div class="flex justify-between items-center border-t border-[#334155] pt-4 mt-6">
                        <div>
                            <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Total de Compra</span>
                            <span class="text-2xl font-black text-emerald-400" id="c-total">S/ 0.00</span>
                        </div>
                        <button type="submit" class="bg-emerald-600 hover:bg-emerald-700 text-[#F8FAFC] font-bold px-6 py-3 rounded-xl transition-colors shadow-lg shadow-emerald-600/20 flex items-center gap-2">
                            <i class="bi bi-box-arrow-in-down"></i> Ingresar al Almacén
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6" data-aos="fade-up">
            <!-- OC Tab -->
            <div class="bg-[#1E293B] rounded-2xl shadow-sm border border-[#334155] overflow-hidden">
                <div class="p-4 border-b border-[#334155] bg-[#111827] flex justify-between items-center">
                    <h3 class="font-bold text-[#F8FAFC]">Historial de Órdenes de Compra</h3>
                    <button class="bg-[#1E293B] hover:bg-[#334155] border border-[#334155] text-xs text-[#CBD5E1] px-2.5 py-1.5 rounded-lg transition-colors" onclick="api.getPurchaseOrders().then(d => exportToCSV(d, 'ordenes_compra.csv'))">
                        <i class="bi bi-download"></i> Exportar
                    </button>
                </div>
                <div class="overflow-x-auto max-h-[300px] custom-scrollbar">
                    <table class="w-full text-left border-collapse">
                        <thead>
                            <tr class="bg-[#111827]/50 border-b border-[#334155]">
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">OC N°</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Proveedor</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Fecha Est.</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Total Est.</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Estado</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-[#334155] text-xs">
                            ${ocRows || '<tr><td colspan="5" class="p-8 text-center text-slate-500">No hay OCs emitidas.</td></tr>'}
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Compras Tab -->
            <div class="bg-[#1E293B] rounded-2xl shadow-sm border border-[#334155] overflow-hidden">
                <div class="p-4 border-b border-[#334155] bg-[#111827] flex justify-between items-center">
                    <h3 class="font-bold text-[#F8FAFC]">Historial de Compras Reales</h3>
                    <button class="bg-[#1E293B] hover:bg-[#334155] border border-[#334155] text-xs text-[#CBD5E1] px-2.5 py-1.5 rounded-lg transition-colors" onclick="api.getPurchases().then(d => exportToCSV(d, 'compras.csv'))">
                        <i class="bi bi-download"></i> Exportar
                    </button>
                </div>
                <div class="overflow-x-auto max-h-[300px] custom-scrollbar">
                    <table class="w-full text-left border-collapse">
                        <thead>
                            <tr class="bg-[#111827]/50 border-b border-[#334155]">
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Factura</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Proveedor</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Fecha</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Total</th>
                                <th class="p-3 text-xs font-bold text-[#CBD5E1] uppercase">Estado</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-[#334155] text-xs">
                            ${crRows || '<tr><td colspan="5" class="p-8 text-center text-slate-500">No hay compras registradas.</td></tr>'}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    // Compra Real Logic
    window.tempCompraItems = [];
    window.linkOC = () => {
        const ocId = document.getElementById('c-order-link').value;
        if(!ocId) return;
        const oc = orders.find(o => o.id == ocId);
        if(!oc) return;
        
        document.getElementById('c-prov').value = oc.providerId;
        window.tempCompraItems = oc.items.map(i => ({...i}));
        renderCompraTable();
    };

    const cProdSelect = document.getElementById('c-prod');
    if (cProdSelect) {
        cProdSelect.addEventListener('change', e => {
            const opt = e.target.options[e.target.selectedIndex];
            document.getElementById('c-cost').value = (opt.getAttribute('data-price') * 0.7).toFixed(2);
        });
        // trigger initial cost
        if (cProdSelect.options[0]) {
            document.getElementById('c-cost').value = (cProdSelect.options[0].getAttribute('data-price') * 0.7).toFixed(2);
        }
    }

    window.addCompraItem = () => {
        const pSel = document.getElementById('c-prod');
        const pId = pSel.value;
        const name = pSel.options[pSel.selectedIndex].text;
        const cant = parseInt(document.getElementById('c-cant').value);
        const cost = parseFloat(document.getElementById('c-cost').value);
        if(!pId || cant<=0 || cost<=0) return;
        
        window.tempCompraItems.push({ productId: parseInt(pId), quantity: cant, cost: cost, name });
        renderCompraTable();
    };

    window.removeCompraItem = (idx) => {
        window.tempCompraItems.splice(idx, 1);
        renderCompraTable();
    };

    window.renderCompraTable = () => {
        document.querySelector('#c-table tbody').innerHTML = window.tempCompraItems.map((i, idx) => `
            <tr class="hover:bg-[#111827]/40 transition-colors border-b border-[#334155] last:border-0">
                <td class="p-3 text-[#F8FAFC]">${i.name||i.productId}</td>
                <td class="p-3 text-[#CBD5E1]">${i.quantity}</td>
                <td class="p-3 text-[#CBD5E1]">${formatMoney(i.cost)}</td>
                <td class="p-3 text-[#F8FAFC] font-semibold">${formatMoney((i.quantity*i.cost))}</td>
                <td class="p-3 text-right">
                    <button type="button" class="p-1.5 text-red-400 hover:bg-red-500/10 rounded-lg transition-all" onclick="removeCompraItem(${idx})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        const total = window.tempCompraItems.reduce((s,i)=>s+(i.quantity*i.cost),0);
        document.getElementById('c-total').textContent = formatMoney(total);
    };

    document.getElementById('compra-form').addEventListener('submit', async e => {
        e.preventDefault();
        if(window.tempCompraItems.length===0) return Swal.fire('Error', 'La tabla de compra está vacía.', 'error');
        await api.savePurchase({
            orderId: parseInt(document.getElementById('c-order-link').value) || null,
            providerId: parseInt(document.getElementById('c-prov').value),
            nroFactura: document.getElementById('c-fac').value,
            date: document.getElementById('c-date').value,
            total: window.tempCompraItems.reduce((s,i)=>s+(i.quantity*i.cost),0),
            items: window.tempCompraItems
        });
        await Swal.fire({
            icon: 'success',
            title: 'Compra registrada',
            text: 'El stock ha sido incrementado en el almacén.',
            confirmButtonColor: '#3b82f6'
        });
        navigate('purchases');
    });
}

window.openOCModal = () => {
    window.tempOCItems = [];
    showModal(`
        <div class="p-6">
            <div class="flex justify-between items-center mb-6">
                <h3 class="text-xl font-bold text-slate-900">Emitir Orden de Compra</h3>
                <button class="text-slate-400 hover:text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-lg p-2 transition-colors" onclick="closeModal(event)"><i class="bi bi-x-lg"></i></button>
            </div>
            
            <p class="text-xs text-slate-500 mb-4 bg-slate-50 border border-slate-200 rounded-xl p-3">
                <i class="bi bi-info-circle text-blue-500 mr-1"></i> Cotiza con tu proveedor. Esto <strong>no aumenta</strong> el stock en el sistema hasta que se registre la compra real.
            </p>
            
            <form id="oc-form" class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-semibold text-slate-700 mb-1">Proveedor</label>
                        <select id="oc-prov" class="w-full rounded-xl border-slate-200 bg-slate-50 focus:border-blue-500 focus:ring-blue-500 py-2.5" required>
                            <option value="">Seleccionar...</option>
                            ${window.comprasContext.provHtml}
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-semibold text-slate-700 mb-1">Fecha Estimada</label>
                        <input type="date" id="oc-date" class="w-full rounded-xl border-slate-200 bg-slate-50 focus:border-blue-500 focus:ring-blue-500 py-2.5" required value="${new Date().toISOString().split('T')[0]}">
                    </div>
                </div>
                
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl">
                    <h4 class="text-xs font-bold text-slate-700 mb-2 uppercase tracking-wide">Añadir Producto a Cotizar</h4>
                    <div class="grid grid-cols-3 gap-3 items-end">
                        <div class="col-span-2">
                            <label class="block text-xs text-slate-500 mb-1">Producto</label>
                            <select id="oc-prod" class="w-full rounded-xl border-slate-200 bg-white focus:border-blue-500 focus:ring-blue-500 py-2.5">
                                ${window.comprasContext.prodHtml}
                            </select>
                        </div>
                        <div>
                            <label class="block text-xs text-slate-500 mb-1">Cant.</label>
                            <div class="flex gap-2">
                                <input type="number" id="oc-cant" class="w-full rounded-xl border-slate-200 bg-white focus:border-blue-500 focus:ring-blue-500 py-2.5" value="1" min="1">
                                <button type="button" class="bg-blue-600 hover:bg-blue-700 text-white px-4 rounded-xl transition-all font-bold" onclick="addOCItem()">
                                    <i class="bi bi-plus-lg"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="overflow-x-auto rounded-xl border border-slate-100">
                    <table class="w-full text-left text-xs border-collapse" id="oc-table">
                        <thead>
                            <tr class="bg-slate-50 border-b border-slate-150">
                                <th class="p-2.5 font-bold text-slate-600">Producto a Cotizar</th>
                                <th class="p-2.5 font-bold text-slate-600">Cant</th>
                                <th class="p-2.5 font-bold text-slate-600">Sub. Est</th>
                                <th class="p-2.5 font-bold text-slate-600 text-right">Acción</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-100">
                            <!-- Items added here -->
                        </tbody>
                    </table>
                </div>
                
                <div class="flex justify-between items-center pt-4 border-t border-slate-100">
                    <div>
                        <span class="text-xs text-slate-500 uppercase tracking-wide block">Total Estimado</span>
                        <span class="text-2xl font-bold text-slate-800" id="oc-total">S/ 0.00</span>
                    </div>
                    <div class="flex gap-2">
                        <button type="button" class="px-5 py-2.5 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 transition-colors" onclick="closeModal()">Cancelar</button>
                        <button type="submit" class="px-5 py-2.5 rounded-xl bg-blue-600 text-white font-bold shadow-md shadow-blue-600/20 hover:bg-blue-700 transition-colors">Emitir Orden de Compra</button>
                    </div>
                </div>
            </form>
        </div>
    `, 'max-w-3xl');

    window.addOCItem = () => {
        const pSel = document.getElementById('oc-prod');
        const pId = pSel.value;
        const name = pSel.options[pSel.selectedIndex].text;
        const cant = parseInt(document.getElementById('oc-cant').value);
        const costEst = pSel.options[pSel.selectedIndex].getAttribute('data-price') * 0.7; // 30% margin approx
        if(!pId || cant<=0) return;
        
        window.tempOCItems.push({ productId: parseInt(pId), quantity: cant, cost: costEst, name });
        renderOCTable();
    };
    
    window.removeOCItem = (idx) => {
        window.tempOCItems.splice(idx, 1);
        renderOCTable();
    };

    window.renderOCTable = () => {
        document.querySelector('#oc-table tbody').innerHTML = window.tempOCItems.map((i, idx) => `
            <tr>
                <td class="p-2.5 font-medium text-slate-800">${i.name}</td>
                <td class="p-2.5 text-slate-600">${i.quantity}</td>
                <td class="p-2.5 text-slate-800 font-bold">${formatMoney((i.quantity*i.cost))}</td>
                <td class="p-2.5 text-right">
                    <button type="button" class="text-red-500 hover:bg-red-50 p-1.5 rounded-lg transition-colors" onclick="removeOCItem(${idx})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        const total = window.tempOCItems.reduce((s,i)=>s+(i.quantity*i.cost),0);
        document.getElementById('oc-total').textContent = formatMoney(total);
    };

    document.getElementById('oc-form').addEventListener('submit', async e => {
        e.preventDefault();
        if(window.tempOCItems.length===0) return Swal.fire('Error', 'La orden de compra está vacía.', 'error');
        await api.savePurchaseOrder({
            providerId: parseInt(document.getElementById('oc-prov').value),
            date: document.getElementById('oc-date').value,
            estimatedTotal: window.tempOCItems.reduce((s,i)=>s+(i.quantity*i.cost),0),
            items: window.tempOCItems
        });
        await Swal.fire({
            icon: 'success',
            title: 'Orden emitida',
            text: 'La orden de compra se guardó en estado PENDIENTE.',
            confirmButtonColor: '#3b82f6'
        });
        closeModal();
        navigate('purchases');
    });
};

window.renderCompras = renderCompras;
