M.AutoInit();
document.addEventListener('DOMContentLoaded', function () {
    var elems = document.querySelectorAll('.sidenav');
    var instances = M.Sidenav.init(elems, {});
});

function renderResponse() {
    const name = document.getElementById("name").value;
    document.getElementById("resultDescription").innerText = `Результат для имени "${name}"`
    const resultDiv = document.getElementById("resultDiv");
    fetch(`/api/names/${name}/info`, {
        method: 'GET',
        headers: {'Content-Type': 'application/json'}
    })
        .then(response => {
            let resultHTML = "<h4></h4>"
            if (response.ok) {
                response.json().then(json => {
                    if (json) {
                        for (let key in json) {
                            if (json.hasOwnProperty(key)) {
                                resultHTML += `<p class="flow-text">${key}:</strong> ${json[key]}</p>`;
                            }
                        }
                        resultDiv.innerHTML = resultHTML;
                    } else {
                        resultDiv.innerHTML = "<p class='flow-text'>Нет информации о данном имени.</p>";
                    }
                })
            } else {
                response.text().then(body => resultDiv.innerHTML = `<p class="flow-text" style="color: red">Ошибка:<br>${body}</p>`)
            }
        })
        .catch(error => {
            // Если запрос не удался
            resultDiv.innerHTML = `<p class="flow-text" style="color: red">Ошибка запроса:<br>${error}</p>`;
        });
}