/**
 * FraudWatch — Windows UI Kit Client Engine
 * Real-time REST API integration via native fetch()
 */

// Global State
const FraudWatch = {
    currentView: 'dashboard',
    refreshInterval: null
};

document.addEventListener('DOMContentLoaded', function () {
    // Initial Load
    initApp();

    // Setup Keyboard Shortcuts (Windows UI Kit Style)
    setupKeyboardShortcuts();

    // Default timestamp picker to current datetime
    setupDateTimePickers();
});

function initApp() {
    // Determine active tab if on single-page view or read view from hash
    const hash = window.location.hash.replace('#', '');
    if (['dashboard', 'transactions', 'rules', 'flagged'].includes(hash)) {
        switchView(hash);
    } else {
        refreshAllData();
    }
}

/**
 * Switch view for Single-Page Application mode
 */
function switchView(viewName) {
    FraudWatch.currentView = viewName;
    window.location.hash = viewName;

    // Update nav links
    document.querySelectorAll('.nav-link-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('data-view') === viewName) {
            btn.classList.add('active');
        }
    });

    // Update view panels
    document.querySelectorAll('.view-section').forEach(section => {
        section.style.display = 'none';
    });

    const activeSection = document.getElementById('view-' + viewName);
    if (activeSection) {
        activeSection.style.display = 'block';
    }

    // Refresh specific section data
    if (viewName === 'dashboard') {
        loadDashboardMetrics();
        loadFlaggedTransactions();
    } else if (viewName === 'transactions') {
        loadTransactions();
    } else if (viewName === 'rules') {
        loadRules();
    } else if (viewName === 'flagged') {
        loadFlaggedTransactions();
    }
}

/**
 * Refresh all live data from backend
 */
async function refreshAllData() {
    try {
        await Promise.all([
            loadDashboardMetrics(),
            loadTransactions(),
            loadRules(),
            loadFlaggedTransactions()
        ]);
    } catch (err) {
        console.error('Error refreshing data:', err);
    }
}

/**
 * 1. DASHBOARD METRICS: Fetch dynamically from the 6 backend endpoints
 */
async function loadDashboardMetrics() {
    try {
        const [totalTx, totalFlagged, pending, blocked, approved, totalReviews] = await Promise.all([
            fetch('/dashboard/total-transactions').then(res => res.json()).catch(() => 0),
            fetch('/dashboard/total-flagged').then(res => res.json()).catch(() => 0),
            fetch('/dashboard/pending').then(res => res.json()).catch(() => 0),
            fetch('/dashboard/blocked').then(res => res.json()).catch(() => 0),
            fetch('/dashboard/approved').then(res => res.json()).catch(() => 0),
            fetch('/dashboard/total-reviews').then(res => res.json()).catch(() => 0)
        ]);

        animateValue('metric-totalTransactions', totalTx);
        animateValue('metric-totalFlagged', totalFlagged);
        animateValue('metric-pendingReviews', pending);
        animateValue('metric-blockedTransactions', blocked);
        animateValue('metric-approvedTransactions', approved);
        animateValue('metric-totalReviews', totalReviews);

        // Also update any fallback elements if present
        const elTotalTx = document.getElementById('totalTransactions');
        if (elTotalTx) elTotalTx.textContent = totalTx;
    } catch (err) {
        console.error('Error loading dashboard metrics:', err);
    }
}

/**
 * Number animation helper for smooth Fluent numbers
 */
function animateValue(elementId, endValue) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.textContent = endValue;
}

/**
 * 2. TRANSACTIONS: Fetch, Add, Delete via REST API
 */
async function loadTransactions() {
    const tbody = document.getElementById('transactionsTableBody');
    if (!tbody) return;

    try {
        const res = await fetch('/transactions');
        if (!res.ok) throw new Error('Failed to fetch transactions');
        const transactions = await res.json();

        const countBadge = document.getElementById('transactionCountBadge');
        if (countBadge) countBadge.textContent = transactions.length;

        tbody.innerHTML = '';

        if (transactions.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="empty-state">
                        <div class="empty-state-icon">💳</div>
                        <h4>No transactions recorded</h4>
                        <p>Use the form above to add your first transaction.</p>
                    </td>
                </tr>
            `;
            return;
        }

        transactions.forEach(tx => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="font-weight: 700; color: #1E1B4B;">#${tx.id}</td>
                <td><span style="font-family: monospace; font-weight: 600; color: #4B5563;">${escapeHtml(tx.sender)}</span></td>
                <td><span style="font-family: monospace; font-weight: 600; color: #4B5563;">${escapeHtml(tx.receiver)}</span></td>
                <td style="font-weight: 700; color: #1E1B4B;">$${Number(tx.amount).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
                <td style="color: #6B7280; font-size: 0.84rem;">${formatDateTime(tx.timestamp)}</td>
                <td style="text-align: right;">
                    <div style="display: inline-flex; gap: 6px;">
                        <button type="button" class="btn btn-secondary btn-sm" onclick="showTransactionDetails(${JSON.stringify(tx).replace(/"/g, '&quot;')})">
                            View
                        </button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="deleteTransaction(${tx.id})">
                            Delete
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Error loading transactions:', err);
    }
}

async function handleAddTransactionSubmit(e) {
    if (e) e.preventDefault();

    const senderInput = document.getElementById('txSender');
    const receiverInput = document.getElementById('txReceiver');
    const amountInput = document.getElementById('txAmount');
    const timestampInput = document.getElementById('txTimestamp');

    const sender = senderInput.value.trim();
    const receiver = receiverInput.value.trim();
    const amount = parseFloat(amountInput.value);
    const timestamp = timestampInput.value ? timestampInput.value : new Date().toISOString().slice(0, 19);

    if (!sender || !receiver || isNaN(amount) || amount <= 0) {
        showWinToast('Validation Error', 'Please specify a valid sender, receiver, and positive transfer amount.', 'danger');
        return;
    }

    try {
        const response = await fetch('/transactions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sender, receiver, amount, timestamp })
        });

        if (!response.ok) {
            throw new Error(`Server returned HTTP ${response.status}`);
        }

        const savedTx = await response.json();
        showWinToast('Transaction Processed', `Tx #${savedTx.id} submitted! Automated fraud rules evaluated.`, 'success');

        // Clear form
        senderInput.value = '';
        receiverInput.value = '';
        amountInput.value = '';
        setupDateTimePickers();

        // Refresh views
        await refreshAllData();
    } catch (err) {
        showWinToast('Submission Failed', err.message, 'danger');
    }
}

async function deleteTransaction(id) {
    if (!confirm(`Are you sure you want to delete Transaction #${id}?`)) return;

    try {
        const response = await fetch(`/transactions/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Deletion failed (record may be referenced by flags)');

        showWinToast('Deleted', `Transaction #${id} removed successfully.`, 'info');
        await refreshAllData();
    } catch (err) {
        showWinToast('Error', err.message, 'danger');
    }
}

/**
 * 3. FRAUD RULES: Fetch, Add, Toggle, Delete via REST API
 */
async function loadRules() {
    const tbody = document.getElementById('rulesTableBody');
    if (!tbody) return;

    try {
        const res = await fetch('/rules');
        if (!res.ok) throw new Error('Failed to fetch rules');
        const rules = await res.json();

        const countBadge = document.getElementById('rulesCountBadge');
        if (countBadge) countBadge.textContent = rules.length;

        tbody.innerHTML = '';

        if (rules.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-state">
                        <div class="empty-state-icon">⚙️</div>
                        <h4>No rules configured</h4>
                        <p>Create a rule above to activate automatic anomaly flagging.</p>
                    </td>
                </tr>
            `;
            return;
        }

        rules.forEach(rule => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="font-weight: 700; color: #1E1B4B;">#${rule.id}</td>
                <td style="font-weight: 700; color: #1E1B4B;">${escapeHtml(rule.ruleName)}</td>
                <td>${rule.amountThreshold > 0 ? '$' + Number(rule.amountThreshold).toLocaleString() : '<span style="color: #9CA3AF;">None</span>'}</td>
                <td>${rule.transactionLimit > 0 ? rule.transactionLimit + ' transfers' : '<span style="color: #9CA3AF;">None</span>'}</td>
                <td>${rule.timeWindowMinutes > 0 ? rule.timeWindowMinutes + ' min' : '<span style="color: #9CA3AF;">None</span>'}</td>
                <td>
                    ${rule.enabled 
                        ? '<span class="badge badge-approved">● ENABLED</span>' 
                        : '<span class="badge badge-gray">○ DISABLED</span>'}
                </td>
                <td style="text-align: right;">
                    <div style="display: inline-flex; gap: 6px;">
                        <button type="button" class="btn btn-secondary btn-sm" onclick="toggleRuleStatus(${rule.id}, ${!rule.enabled}, ${JSON.stringify(rule).replace(/"/g, '&quot;')})">
                            ${rule.enabled ? 'Disable' : 'Enable'}
                        </button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="deleteRule(${rule.id})">
                            Delete
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Error loading rules:', err);
    }
}

async function handleAddRuleSubmit(e) {
    if (e) e.preventDefault();

    const nameInput = document.getElementById('ruleNameInput');
    const amountInput = document.getElementById('ruleAmountThreshold');
    const limitInput = document.getElementById('ruleTransactionLimit');
    const windowInput = document.getElementById('ruleTimeWindowMinutes');
    const enabledInput = document.getElementById('ruleEnabledSwitch');

    const ruleName = nameInput.value.trim();
    const amountThreshold = parseFloat(amountInput.value) || 0;
    const transactionLimit = parseInt(limitInput.value) || 0;
    const timeWindowMinutes = parseInt(windowInput.value) || 0;
    const enabled = enabledInput ? enabledInput.checked : true;

    if (!ruleName) {
        showWinToast('Validation Error', 'Rule Name is required.', 'danger');
        return;
    }

    try {
        const response = await fetch('/rules', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                ruleName,
                amountThreshold,
                transactionLimit,
                timeWindowMinutes,
                enabled
            })
        });

        if (!response.ok) throw new Error('Failed to create rule');

        showWinToast('Rule Created', `Rule "${ruleName}" successfully configured.`, 'success');

        nameInput.value = '';
        amountInput.value = '';
        limitInput.value = '';
        windowInput.value = '';

        await loadRules();
    } catch (err) {
        showWinToast('Error', err.message, 'danger');
    }
}

async function toggleRuleStatus(id, newStatus, currentRule) {
    try {
        const payload = Object.assign({}, currentRule, { enabled: newStatus });
        const res = await fetch(`/rules/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('Failed to update rule');
        showWinToast('Rule Updated', `Rule #${id} is now ${newStatus ? 'ENABLED' : 'DISABLED'}.`, 'info');
        await loadRules();
    } catch (err) {
        showWinToast('Error', err.message, 'danger');
    }
}

async function deleteRule(id) {
    if (!confirm(`Are you sure you want to delete Rule #${id}?`)) return;

    try {
        const res = await fetch(`/rules/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Rule deletion failed (may be linked to flagged items)');

        showWinToast('Rule Removed', `Rule #${id} deleted.`, 'info');
        await loadRules();
    } catch (err) {
        showWinToast('Error', err.message, 'danger');
    }
}

/**
 * 4. FLAGGED TRANSACTIONS & MANUAL REVIEW (Approve & Block via POST /review-outcomes)
 */
async function loadFlaggedTransactions() {
    const tbody = document.getElementById('flaggedTableBody');
    const dashTbody = document.getElementById('dashboardFlaggedTableBody');

    try {
        const res = await fetch('/flagged-transactions');
        if (!res.ok) throw new Error('Failed to fetch flagged transactions');
        const flags = await res.json();

        const countBadge = document.getElementById('flaggedCountBadge');
        if (countBadge) countBadge.textContent = flags.length;

        // Render to flagged page table if present
        if (tbody) renderFlaggedRows(tbody, flags, false);

        // Render to dashboard summary table if present
        if (dashTbody) renderFlaggedRows(dashTbody, flags, true);
    } catch (err) {
        console.error('Error loading flagged transactions:', err);
    }
}

function renderFlaggedRows(targetTbody, flags, isCompact) {
    targetTbody.innerHTML = '';

    if (flags.length === 0) {
        targetTbody.innerHTML = `
            <tr>
                <td colspan="${isCompact ? 6 : 8}" class="empty-state">
                    <div class="empty-state-icon">🛡️</div>
                    <h4>No flagged transactions</h4>
                    <p>All transactions are currently within normal thresholds.</p>
                </td>
            </tr>
        `;
        return;
    }

    flags.forEach(flag => {
        const isPending = flag.status === 'PENDING';
        const isApproved = flag.status === 'APPROVED';
        const isBlocked = flag.status === 'BLOCKED';

        let badgeHtml = `<span class="badge badge-purple">${escapeHtml(flag.status)}</span>`;
        if (isPending) badgeHtml = `<span class="badge badge-pending">PENDING</span>`;
        if (isApproved) badgeHtml = `<span class="badge badge-approved">APPROVED</span>`;
        if (isBlocked) badgeHtml = `<span class="badge badge-blocked">BLOCKED</span>`;

        const tr = document.createElement('tr');

        if (isCompact) {
            tr.innerHTML = `
                <td style="font-weight: 700; color: #1E1B4B;">#${flag.id}</td>
                <td><strong style="color: #7C5CFC;">Tx #${flag.transaction ? flag.transaction.id : '-'}</strong></td>
                <td>Rule #${flag.rule ? flag.rule.id : '-'} (${escapeHtml(flag.rule ? flag.rule.ruleName : '')})</td>
                <td style="color: #4B5563; font-size: 0.84rem;">${escapeHtml(flag.reason)}</td>
                <td>${badgeHtml}</td>
                <td style="text-align: right;">
                    ${isPending ? `
                        <div style="display: inline-flex; gap: 6px;">
                            <button type="button" class="btn btn-success btn-sm" onclick="reviewFlaggedTransaction(${flag.id}, 'APPROVED')">
                                Approve
                            </button>
                            <button type="button" class="btn btn-danger btn-sm" onclick="reviewFlaggedTransaction(${flag.id}, 'BLOCKED')">
                                Block
                            </button>
                        </div>
                    ` : `
                        <span style="font-size: 0.78rem; font-weight: 600; color: #9CA3AF;">Resolved</span>
                    `}
                </td>
            `;
        } else {
            tr.innerHTML = `
                <td style="font-weight: 700; color: #1E1B4B;">#${flag.id}</td>
                <td><strong style="color: #7C5CFC;">Tx #${flag.transaction ? flag.transaction.id : '-'}</strong></td>
                <td><span style="font-family: monospace;">${escapeHtml(flag.transaction ? flag.transaction.sender : '')}</span></td>
                <td><span style="font-family: monospace;">${escapeHtml(flag.transaction ? flag.transaction.receiver : '')}</span></td>
                <td style="font-weight: 700;">$${flag.transaction ? Number(flag.transaction.amount).toLocaleString(undefined, { minimumFractionDigits: 2 }) : '0.00'}</td>
                <td>Rule #${flag.rule ? flag.rule.id : '-'}</td>
                <td style="color: #4B5563; font-size: 0.84rem; max-width: 260px;">${escapeHtml(flag.reason)}</td>
                <td>${badgeHtml}</td>
                <td style="text-align: right;">
                    ${isPending ? `
                        <div style="display: inline-flex; gap: 6px;">
                            <button type="button" class="btn btn-success btn-sm" onclick="reviewFlaggedTransaction(${flag.id}, 'APPROVED')">
                                Approve
                            </button>
                            <button type="button" class="btn btn-danger btn-sm" onclick="reviewFlaggedTransaction(${flag.id}, 'BLOCKED')">
                                Block
                            </button>
                        </div>
                    ` : `
                        <span style="font-size: 0.78rem; font-weight: 600; color: #9CA3AF;">Completed</span>
                    `}
                </td>
            `;
        }

        targetTbody.appendChild(tr);
    });
}

/**
 * Trigger POST /review-outcomes to approve or block flagged anomaly
 */
async function reviewFlaggedTransaction(flagId, outcome) {
    const comment = prompt(`Provide an optional audit note for marking Flag #${flagId} as ${outcome}:`, `Manual compliance review: ${outcome}`);
    if (comment === null) return; // User cancelled prompt

    const payload = {
        flaggedTransaction: { id: flagId },
        outcome: outcome,
        comment: comment.trim() || `Manual compliance decision: ${outcome}`,
        reviewedAt: new Date().toISOString()
    };

    try {
        const response = await fetch('/review-outcomes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`Review failed with status ${response.status}`);
        }

        const result = await response.json();
        showWinToast('Decision Recorded', `Flag #${flagId} successfully marked as ${outcome}!`, outcome === 'APPROVED' ? 'success' : 'danger');

        // Refresh all metrics and flags
        await refreshAllData();
    } catch (err) {
        showWinToast('Review Failed', err.message, 'danger');
    }
}

/**
 * Windows UI Kit Toast Notifications
 */
function showWinToast(title, message, type = 'info') {
    let container = document.getElementById('winToastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'winToastContainer';
        container.className = 'toast-container-win';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `win-toast toast-${type}`;
    toast.innerHTML = `
        <div style="font-size: 1.25rem;">
            ${type === 'success' ? '✅' : (type === 'danger' ? '🚫' : 'ℹ️')}
        </div>
        <div class="toast-content" style="flex-grow: 1;">
            <h5>${escapeHtml(title)}</h5>
            <p>${escapeHtml(message)}</p>
        </div>
        <button type="button" style="background: none; border: none; font-size: 1rem; color: #9CA3AF; cursor: pointer;" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 4500);
}

/**
 * Transaction Details Modal / View
 */
function showTransactionDetails(tx) {
    alert(`Transaction Details:\nID: #${tx.id}\nSender: ${tx.sender}\nReceiver: ${tx.receiver}\nAmount: $${Number(tx.amount).toFixed(2)}\nTimestamp: ${tx.timestamp}`);
}

/**
 * Preset Buttons for Fraud Rules
 */
function applyRulePreset(type) {
    const nameInput = document.getElementById('ruleNameInput') || document.getElementById('ruleName');
    const amountInput = document.getElementById('ruleAmountThreshold') || document.getElementById('amountThreshold');
    const limitInput = document.getElementById('ruleTransactionLimit') || document.getElementById('transactionLimit');
    const windowInput = document.getElementById('ruleTimeWindowMinutes') || document.getElementById('timeWindowMinutes');
    const enabledInput = document.getElementById('ruleEnabledSwitch') || document.getElementById('enabled');

    if (type === 'HIGH_AMOUNT') {
        if (nameInput) nameInput.value = 'High Amount Rule';
        if (amountInput) amountInput.value = '10000';
        if (limitInput) limitInput.value = '0';
        if (windowInput) windowInput.value = '0';
        if (enabledInput) enabledInput.checked = true;
    } else if (type === 'VELOCITY') {
        if (nameInput) nameInput.value = 'Rapid Multi-Transaction Rule';
        if (amountInput) amountInput.value = '0';
        if (limitInput) limitInput.value = '3';
        if (windowInput) windowInput.value = '15';
        if (enabledInput) enabledInput.checked = true;
    }
}

/**
 * Keyboard shortcuts (Windows UI Kit Style)
 */
function setupKeyboardShortcuts() {
    window.addEventListener('keydown', function (e) {
        // Ignore if user is typing in input or textarea
        if (['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) return;

        if (e.key === '1') {
            switchView('dashboard');
        } else if (e.key === '2') {
            switchView('transactions');
        } else if (e.key === '3') {
            switchView('rules');
        } else if (e.key === '4') {
            switchView('flagged');
        } else if (e.key.toLowerCase() === 'r') {
            refreshAllData();
            showWinToast('Refreshed', 'All data refreshed from MySQL database.', 'info');
        }
    });
}

function setupDateTimePickers() {
    const pickers = document.querySelectorAll('input[type="datetime-local"]');
    pickers.forEach(p => {
        if (!p.value) {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            p.value = now.toISOString().slice(0, 16);
        }
    });
}

function formatDateTime(isoString) {
    if (!isoString) return '-';
    try {
        const d = new Date(isoString);
        return d.toLocaleString(undefined, {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', second: '2-digit'
        });
    } catch {
        return isoString;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
