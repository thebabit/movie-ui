window.onload = function () {
    document.getElementById('to-login').addEventListener('click', login);
    document.getElementById('to-register').addEventListener('click', checkInput);
    document.getElementById('to-logout').addEventListener('click', logout);
    document.getElementById('alert-msg').style.display = "none";
    document.getElementById('alert-msg-register').style.display = "none";
    document.getElementById('alert-msg-success').style.display = "none";
    document.getElementById('register-failed-user').style.display = "none";

    document.getElementById('pwd').addEventListener('keyup', function (event) {
        if (event.keyCode === 13) {
            login();
        }
    });
    document.getElementById('register-email').addEventListener('keyup', function (event) {
        if (event.keyCode === 13) {
            checkInput();
        }
    });
}

function logout() {
    localStorage.removeItem("jwt");
    location.reload();
}

async function login() {
    console.log('in login()');
    let spinner = document.createElement("span");
    spinner.setAttribute("class", "spinner-border spinner-border-sm");
    document.getElementById("to-login").appendChild(spinner);
    let credentials = [];
    credentials.push(document.getElementById('usr').value);
    credentials.push(document.getElementById('pwd').value);

    let response = await fetch('auth', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    });

    if (response.status == 200) {
        document.getElementById('alert-msg').style.display = "none";
        localStorage.setItem('jwt', response.headers.get('Authorization'));
        console.log(localStorage.getItem('jwt', response.headers.get('Authorization')));
        $('#loginModal').modal('hide');
        loadEmployeeDashboard();
    } else {
        document.getElementById('alert-msg').style.display = "block";
        document.getElementById("to-login").removeChild(spinner);
    }

}

//-------------------------------------------------------------------------------------

/*
    Register component
        - loadRegister()
        - configureRegister()
        - validateUsername()
        - validatePassword()
        - register()
*/

async function register() {
    console.log('in register()');
    document.getElementById('alert-msg-register').style.display = "none";
    document.getElementById('alert-msg-success').style.display = "none";
    let newUser = {
        id: 0,
        username: document.getElementById('register-username').value,
        password: document.getElementById('register-password').value,
        firstname: document.getElementById('register-fn').value,
        lastname: document.getElementById('register-ln').value,
        email: document.getElementById('register-email').value
    };

    let response = await fetch('users', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUser)
    });

    
    if (response.status == 200) {
        document.getElementById('alert-msg-success').style.display = "block";
        document.getElementById('alert-msg-register').style.display = "none";
        $('#regModal').modal('hide');
    } else {
        document.getElementById('register-failed-user').style.display = "block";
    }
    let responseBody = await response.json();
    console.log(responseBody);
}

function checkInput() {
    document.getElementById('alert-msg-register').style.display = "none";

    if(
        (document.getElementById('register-username').value == '') 
        ||(document.getElementById('register-password').value == '') 
        ||(document.getElementById('register-fn').value == '')
        ||(document.getElementById('register-ln').value == '')
        ||(document.getElementById('register-email').value == '')
        ){
            console.log("failed check input");
            document.getElementById('alert-msg-register').style.display = "block";
        
        }else{
        register();
    }

}

//-------------------------------------------------------------------------------------

/*
    Dashboard component
        - loadDashboard()
 */

async function loadEmployeeDashboard() {
    console.log('in loadEmployeeDashboard()');
    APP_VIEW.innerHTML = await fetchView('employee.view');
    DYNAMIC_CSS_LINK.href = 'css/employee.css';
    configureDashboard();
};

async function configureDashboard() {
    console.log('in configureDashboard()');
    document.getElementById('to-add-reimb').addEventListener('click', checkReimb);
    document.getElementById('alert-msg-incomplete').style.display = "none";
    let response = await fetch('reimb', {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('jwt')
        }
    });

    if (response.status == 200) {
        let responseBody = await response.json();
        localStorage.setItem('role', response.headers.get('role'));
        console.log(responseBody);
        populateTable(responseBody);
    }
}

async function populateTable(response) {
    console.log("Inside of populateTable")
    for (let i = 0; i < response.length; i++) {
        console.log(response[i]);
        let row = document.createElement('tr');
        row.setAttribute('id', 'row-' + i);
        console.log(response[i].desc);
        let resp = response[i];

        //let type = resp[i].type;
        let type = resp.typeId;
        switch (type) {
            case (1):
                type = "Lodging";
                break;
            case (2):
                type = "Travel";
                break;
            case (3):
                type = "Food";
                break;
            case (4):
                type = "Other";
                break;
        }
        console.log("Status id is " + response[i].statusId);
        if (response[i].statusId == 3) {
            console.log(response[i]);
            let id = document.createElement('td');
            id.innerText = resp.reimbId;
            row.appendChild(id);

            let amount = document.createElement('td');
            amount.innerHTML = resp.amount
            row.appendChild(amount);

            let desc = document.createElement('td');
            desc.innerHTML = resp.desc
            row.appendChild(desc);

            let reimbType = document.createElement('td');
            reimbType.innerHTML = type
            row.appendChild(reimbType);

            let author = document.createElement('td');
            author.innerHTML = resp.author
            row.appendChild(author);

            let submitted = document.createElement('td');
            submitted.innerHTML = resp.submitted
            row.appendChild(submitted);

            if (localStorage.getItem('role') == "admin") {
                let approve = document.createElement('td');
                approve.innerText = "Approve";
                approve.setAttribute("id", "approve-button" + i);
                approve.setAttribute("class", "btn btn-success");
                row.appendChild(approve);

                let deny = document.createElement('td');
                deny.innerText = "Deny";
                deny.setAttribute("id", "deny-button" + i);
                deny.setAttribute("class", "btn btn-danger");
                row.appendChild(deny);


            }
            document.getElementById("pending-table-body").append(row);
        }

        if (response[i].statusId == 1 || response[i].statusId == 2) {
            let reimbursementStatus = "Approved";
            if (response[i].statusId == 1) {
                reimbursementStatus = "Approved"
            } else if (response[i].statusId == 2) {
                reimbursementStatus = "Denied"
            }
            console.log(response[i]);
            let id = document.createElement('td');
            id.innerText = resp.reimbId;
            row.appendChild(id);

            let amount = document.createElement('td');
            amount.innerHTML = resp.amount;
            row.appendChild(amount);

            let desc = document.createElement('td');
            desc.innerHTML = resp.desc;
            row.appendChild(desc);

            let reimbType = document.createElement('td');
            reimbType.innerHTML = type;
            row.appendChild(reimbType);

            let author = document.createElement('td');
            author.innerHTML = resp.author;
            row.appendChild(author);

            let submitted = document.createElement('td');
            submitted.innerHTML = resp.submitted;
            row.appendChild(submitted);

            let reimbStatus = document.createElement('td');
            reimbStatus.innerHTML = reimbursementStatus;
            row.appendChild(reimbStatus);

            let resolver = document.createElement('td');
            resolver.innerHTML = resp.resolver;
            row.appendChild(resolver);

            let resolved = document.createElement('td');
            resolved.innerHTML = resp.resolved;
            row.appendChild(resolved);
            document.getElementById("past-table-body").append(row);
        }
        if (response[i].statusId == 3 && localStorage.getItem('role') == "admin") {
            document.getElementById("approve-button" + i).addEventListener('click', approve);
            document.getElementById("deny-button" + i).addEventListener('click', deny);
        }
    }
}


async function approve() {
    console.log("inside of approve");
    let x = event.target.id + ""
    let tryme = event.target.id + "";
    tryme.slice(tryme.length - 1);
    console.log(tryme);
    x = x.slice(x.length - 1);
    console.log("x is currently  " + x);
    let body = document.getElementById('row-' + x);
    console.log("inner text " + body.childNodes[0].innerText);
    console.log("maybe array value " + body.innerText);
    let updateType = body.childNodes[3].innerText
    switch (updateType) {
        case ("Lodging"):
            updateType = 1;
            break;
        case ("Food"):
            updateType = 3;
            break;
        case ("Travel"):
            updateType = 2;
            break;
        case ("Other"):
            updateType = 4;
            break;
    }

    let updateReimb = {
        reimbId: body.childNodes[0].innerText,
        amount: body.childNodes[1].innerText,
        desc: body.childNodes[2].innerText,
        typeId: updateType,
        author: body.childNodes[4].innerText,
        submitted: body.childNodes[5].innerText,
        statusId: 1
    };

    let response = await fetch('update', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('jwt')
        },
        body: JSON.stringify(updateReimb)
    });

    if (response.status == 200) {
        console.log("Reimbursement Accepted")
        clearTable();
    } else {
        console.log("HAAA HAA!")
        console.log("FAAAAAAAAAAAILLLUUUUUUUUUUUUUURE")
    }
}

async function deny() {
    console.log("inside of deny");
    let x = event.target.id + ""
    let tryme = event.target.id + "";
    tryme.slice(tryme.length - 1);
    console.log(tryme);
    x = x.slice(x.length - 1);
    console.log("x is currently  " + x);
    let body = document.getElementById('row-' + x);
    console.log("inner text " + body.childNodes[0].innerText);
    console.log("maybe array value " + body.innerText);
    let updateType = body.childNodes[3].innerText
    switch (updateType) {
        case ("Lodging"):
            updateType = 1;
            break;
        case ("Food"):
            updateType = 3;
            break;
        case ("Travel"):
            updateType = 2;
            break;
        case ("Other"):
            updateType = 4;
            break;
    }

    let updateReimb = {
        reimbId: body.childNodes[0].innerText,
        amount: body.childNodes[1].innerText,
        desc: body.childNodes[2].innerText,
        typeId: updateType,
        author: body.childNodes[4].innerText,
        submitted: body.childNodes[5].innerText,
        statusId: 2
    };

    let response = await fetch('update', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('jwt')
        },
        body: JSON.stringify(updateReimb)
    });

    if (response.status == 200) {
        console.log("Denial went through")
        clearTable();
    } else {
        console.log("HAAA HAA!")
        console.log("FAAAAAAAAAAAILLLUUUUUUUUUUUUUURE")
    }
}

async function newReimb() {
    console.log('in newReimb()');

    let reimbType = document.getElementById('sel1').value;
    switch (reimbType) {
        case ("Lodging"):
            reimbType = 1;
            break;
        case ("Food"):
            reimbType = 3;
            break;
        case ("Travel"):
            reimbType = 2;
            break;
        case ("Other"):
            reimbType = 4;
            break;
    }
    let newReimb = {

        amount: document.getElementById('amount').value,
        desc: document.getElementById('desc').value,
        typeId: reimbType
    };

    // POST information to servlet
    let response = await fetch('reimb', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('jwt')
        },
        body: JSON.stringify(newReimb)
    });

    let responseBody = await response.json();
    console.log(responseBody);
    console.log(newReimb);
    console.log(response);

    if (response.status == 200) {
        $('#reimbModal').modal('hide');
        clearTable();
    }

}

function checkReimb() {
    document.getElementById('alert-msg-incomplete').style.display = "none";

    if(
        (document.getElementById('amount').value == '') 
        ||(document.getElementById('desc').value == '')
        ){
            console.log("failed check input");
            document.getElementById('alert-msg-incomplete').style.display = "block";
        
        }else{
            newReimb();
    }

}

function clearTable() {
    let past = document.getElementById("past-table-body");
    let pending = document.getElementById("pending-table-body")
    past.innerHTML = "";
    pending.innerHTML = "";

    loadEmployeeDashboard();
}

async function viewReim() {
    console.log('in viewReim');

    APP_VIEW.innerHTML = await fetchView('view-reim.view');
    DYNAMIC_CSS_LINK.href = 'css/app.css';
}
//-------------------------------------------------------------------------------------
async function fetchView(uri) {
    console.log(localStorage.getItem('jwt'));
    let response = await fetch(uri, {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Authorization': localStorage.getItem('jwt')
        }
    });

    if (response.status == 401) loadLogin();
    return await response.text();
}

//-------------------------------------------------------------------------------------

const APP_VIEW = document.getElementById('app-view');
const DYNAMIC_CSS_LINK = document.getElementById('dynamic-css');