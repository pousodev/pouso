function getParams() {
    var p = new URLSearchParams(window.location.search);
    return {
        page: parseInt(p.get('page')) || 0,
        size: parseInt(p.get('size')) || 10,
        sortBy: p.get('sortBy') || 'nome',
        sortDir: p.get('sortDir') || 'asc',
        q: p.get('q') || ''
    };
}

function buildUrl(params) {
    var u = new URL(window.location.origin + window.location.pathname);
    if (params.sortBy) u.searchParams.set('sortBy', params.sortBy);
    if (params.sortDir) u.searchParams.set('sortDir', params.sortDir);
    if (params.size) u.searchParams.set('size', params.size);
    if (params.page !== undefined) u.searchParams.set('page', params.page);
    if (params.q) u.searchParams.set('q', params.q);
    return u.toString();
}

function applyFilter(sortBy, sortDir) {
    var p = getParams();
    p.sortBy = sortBy;
    p.sortDir = sortDir;
    p.page = 0;
    window.location = buildUrl(p);
}

function applySize(size) {
    var p = getParams();
    p.size = size;
    p.page = 0;
    window.location = buildUrl(p);
}

function goToPage(page) {
    var p = getParams();
    p.page = page;
    window.location = buildUrl(p);
}

(function setupSearch() {
    var input = document.getElementById('search-input');
    var clearBtn = document.getElementById('clear-search');
    var sidebarSearchBtn = document.getElementById('sidebar-search-button');
    if (!input) return;

    function applySearch() {
        var p = getParams();
        p.q = input.value.trim();
        p.page = 0;
        window.location = buildUrl(p);
    }

    input.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            applySearch();
        }
    });

    if (clearBtn) {
        input.addEventListener('input', function() {
            clearBtn.style.display = input.value.trim() ? '' : 'none';
        });
    }

    if (sidebarSearchBtn) {
        sidebarSearchBtn.addEventListener('click', applySearch);
    }

    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            input.value = '';
            clearBtn.style.display = 'none';
            var p = getParams();
            p.q = '';
            p.page = 0;
            window.location = buildUrl(p);
        });
    }
})();
