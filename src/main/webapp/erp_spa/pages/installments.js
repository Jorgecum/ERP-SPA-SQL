async function renderInstallments(c) {
    const insts = await api.getInstallments();
    const sales = await api.getSales();
    const clients = await api.getEntities();
    
    // Group installments by saleId
    const salesWithInsts = [...new Set(insts.map(i => i.saleId))];
    
    let html = '';
    
    for (let sId of salesWithInsts) {
        const sale = sales.find(s => s.id === sId) || { correlative: 'Venta ' + sId, total: 0 };
        const client = clients.find(cl => cl.id === sale.clientId) || { name: 'Cliente Mostrador' };
        const sInsts = insts.filter(i => i.saleId === sId).sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
        
        let pendingBalance = 0;
        let totalDebt = 0;
        
        const rows = sInsts.map((i, idx) => {
            const p = i.amount - (i.paidAmount || 0);
            if (i.status !== 'Cancelada') pendingBalance += p;
            totalDebt += i.amount;
            
            let badge = '';
            let dot = '';
            if (i.status === 'Cancelada') {
                badge = 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
                dot = 'bg-emerald-500';
            } else if (i.status === 'Parcial') {
                badge = 'bg-sky-500/20 text-sky-400 border border-sky-500/30';
                dot = 'bg-sky-500';
            } else {
                badge = 'bg-amber-500/20 text-amber-400 border border-amber-500/30';
                dot = 'bg-amber-500';
            }
            
            return `
                <tr class="hover:bg-[#111827]/40 transition-colors border-b border-[#334155] last:border-0">
                    <td class="p-3 font-semibold text-[#F8FAFC]">Cuota ${idx+1}</td>
                    <td class="p-3 text-[#CBD5E1] font-mono">${i.dueDate}</td>
                    <td class="p-3 text-[#F8FAFC] font-medium">${formatMoney(i.amount)}</td>
                    <td class="p-3 text-emerald-400 font-bold">${formatMoney((i.paidAmount || 0))}</td>
                    <td class="p-3 text-red-400 font-bold">${formatMoney(p)}</td>
                    <td class="p-3 whitespace-nowrap">
                        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold ${badge}">
                            <span class="w-1.5 h-1.5 rounded-full ${dot}"></span>
                            ${i.status}
                        </span>
                    </td>
                </tr>
            `;
        }).join('');
        
        html += `
            <div class="bg-[#1E293B] rounded-2xl shadow-sm border border-[#334155] p-6 mb-6" data-aos="fade-up">
                <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-4 pb-3 border-b border-[#334155]">
                    <div>
                        <h3 class="font-bold text-[#F8FAFC] text-base">Comprobante: ${sale.correlative}</h3>
                        <p class="text-xs text-[#CBD5E1] mt-1">Cliente: <strong>${client.name}</strong> | Total Deuda: ${formatMoney(totalDebt)} | <span class="text-red-400 font-bold">Saldo Pendiente: ${formatMoney(pendingBalance)}</span></p>
                    </div>
                    ${pendingBalance > 0 ? `
                        <button class="bg-blue-600 hover:bg-blue-700 text-[#F8FAFC] px-4 py-2 rounded-xl font-semibold shadow-sm shadow-blue-600/20 transition-all flex items-center gap-1.5 text-sm" onclick="openAbonoModal(${sId}, ${pendingBalance})">
                            <i class="bi bi-wallet2"></i> Registrar Abono
                        </button>
                    ` : `
                        <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                            Venta Cancelada
                        </span>
                    `}
                </div>
                <div class="overflow-x-auto rounded-xl border border-[#334155]">
                    <table class="w-full text-left text-xs border-collapse">
                        <thead>
                            <tr class="bg-[#111827] border-b border-[#334155]">
                                <th class="p-3 font-bold text-[#CBD5E1]">Cuota</th>
                                <th class="p-3 font-bold text-[#CBD5E1]">Vencimiento</th>
                                <th class="p-3 font-bold text-[#CBD5E1]">Monto Total</th>
                                <th class="p-3 font-bold text-[#CBD5E1]">Pagado</th>
                                <th class="p-3 font-bold text-[#CBD5E1]">Saldo</th>
                                <th class="p-3 font-bold text-[#CBD5E1]">Estado</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-[#334155]">
                            ${rows}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    }

    c.innerHTML = `
        <div class="mb-6 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4" data-aos="fade-down">
            <div>
                <h2 class="text-2xl font-bold text-[#F8FAFC] tracking-tight">Gestión de Cuotas y Pagos</h2>
                <p class="text-sm text-[#CBD5E1] mt-1">Control de cuentas por cobrar. Los abonos registrados se distribuyen automáticamente cubriendo las cuotas más antiguas primero.</p>
            </div>
        </div>
        ${html || '<div class="bg-[#1E293B] rounded-2xl shadow-sm border border-[#334155] p-12 text-center text-[#CBD5E1]"><i class="bi bi-journal-x text-3xl opacity-50 block mb-2"></i>No hay ventas a crédito registradas.</div>'}
    `;
}

window.openAbonoModal = (saleId, pendingBalance) => {
    showModal(`
        <div class="p-6 text-slate-800">
            <div class="flex justify-between items-center mb-6">
                <h3 class="text-xl font-bold text-slate-900">Registrar Abono a la Venta</h3>
                <button class="text-slate-400 hover:text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-lg p-2 transition-colors" onclick="closeModal(event)"><i class="bi bi-x-lg"></i></button>
            </div>
            
            <p class="text-xs text-slate-500 mb-4 bg-slate-50 border border-slate-200 rounded-xl p-3">
                <i class="bi bi-info-circle text-blue-500 mr-1"></i> El abono se distribuirá de forma cronológica sobre las cuotas pendientes de esta venta.
            </p>
            
            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-semibold text-slate-700 mb-1">Monto a Abonar (S/)</label>
                    <input type="number" id="abono-amount" class="w-full rounded-xl border-slate-200 bg-slate-50 focus:border-blue-500 focus:ring-blue-500 py-3 text-2xl font-black text-blue-600 text-center" value="${pendingBalance.toFixed(2)}" step="0.01" max="${pendingBalance}" min="0.01">
                    <small class="text-xs text-slate-500 mt-1.5 block text-center">Saldo pendiente máximo: ${formatMoney(pendingBalance)}</small>
                </div>
                
                <div class="flex justify-end gap-3 pt-4 border-t border-slate-100">
                    <button class="px-5 py-2.5 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 transition-colors" onclick="closeModal()">Cancelar</button>
                    <button class="px-5 py-2.5 rounded-xl bg-blue-600 text-white font-bold shadow-md shadow-blue-600/20 hover:bg-blue-700 transition-colors" onclick="confirmAbono(${saleId})">Procesar Abono</button>
                </div>
            </div>
        </div>
    `, 'max-w-md');
};

window.confirmAbono = async (saleId) => {
    const amount = parseFloat(document.getElementById('abono-amount').value);
    if(amount > 0) {
        await api.paySaleInstallments(saleId, amount);
        closeModal();
        await Swal.fire({
            icon: 'success',
            title: 'Abono Procesado',
            text: 'Abono registrado y distribuido correctamente sobre las cuotas.',
            confirmButtonColor: '#3b82f6'
        });
        navigate('installments');
    } else {
        Swal.fire('Error', 'Ingrese un monto válido.', 'error');
    }
};

window.renderInstallments = renderInstallments;
