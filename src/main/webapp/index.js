// TODO: refactor into utils.js. Or nevermind I don't care. This is garbage
function get_cookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        return parts.pop().split(';').shift();
    }
    return null;
}
function assert_non_null(a, ...fmt) {
    if(a == null) {
        throw new Error("Assertion on null value " + fmt.join(" "))
    }
    return a;
}
const send_to_login_page = () => location.href = "login.html";
async function make_json_request(filename, body, method='POST') {
    return await fetch(filename, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });
}
async function make_access_token_request(filename, token, method='POST') {
    assert_non_null(token, "Called make_access_token_request without specifying token");
    return await make_json_request(filename, {
        access_token: token
    }, method);
}

const admin_request_get_accounts = (token) => make_access_token_request('/admin_rq/get_users', token);
const delivery_get_pending_orders = (token) => make_access_token_request('/delivery_rq/pending_orders', token);

const make_account_info_request = (token) => make_access_token_request('/account_info', token);
const request_get_products = (token, restaurant_id) => make_json_request('/get_products', {
    access_token: token,
    restaurant_id: restaurant_id,
});
const request_get_restaurants = (token) => make_access_token_request('/get_restaurants', token);

function table_from_object_array(arr) {
    let body = "";
    assert_non_null(arr[0]);
    const headers = Object.keys(arr[0]).map((key) => `<th>${key}<th>`).join("");
    body += `<table>\n<thead><tr>${headers}</tr></thead><tbody>`;
    for (const obj of arr) {
        const entry = Object.values(obj).map((key) => `<td>${key}<td>`).join("");
        body += `<tr>${entry}</tr>`;
    }
    body += `</tbody></table>`;
    return body;
}
function product_feature_from_array(restaurant_id, title, selection) {
    return `<section class="product-section">
              <h2 class="section-title">${title}</h2>
              <div class="product-scroll">
                ${selection.map((key, idx) => `<button class="product-card" onclick="pick_product(${restaurant_id}, ${idx})">
                        ${key.name}
                        <br>
                        €${key.price}
                    </h6>
                </button>`).join("\n")}
              </div>
            </section>`;
}
const button_for_render_state = (name, idx) => `<button onclick="render_state=${idx}; render_site()" '>${name}</button>`;

let iota = 0;

iota = 0;
const ADMIN_RENDER_STATE_ACCOUNTS = iota++;
const ADMIN_RENDER_STATE_RESTAURANTS = iota++;
const ADMIN_RENDER_STATE_PRODUCTS = iota++;
const ADMIN_RENDER_STATE_COUNT = iota++;

iota = 0;
const CUSTOMER_RENDER_STATE_MENU = iota++;
const CUSTOMER_RENDER_STATE_CART = iota++;

iota = 0;
const DELIVERY_RENDER_STATE_ORDERS = iota++;

// NOTE: zero is assumed to be the default render state for everything
let render_state = 0;
let account_info = {};
let access_token = null;
// TODO: save in cookies and invalidate on login.
let cart = {};

function pick_product(restaurant_id, product_id) {
    if(!(restaurant_id in cart)) cart[restaurant_id] = {};
    if(!(product_id in cart[restaurant_id])) cart[restaurant_id][product_id] = 0;
    cart[restaurant_id][product_id]++;
    render_site();
}
function cart_update_count(restaurant_id, product_id, update) {
    if(update == -1 && cart[restaurant_id][product_id] == 1) {
        delete cart[restaurant_id][product_id]
        if(Object.entries(cart[restaurant_id]).length == 0) {
            delete cart[restaurant_id];
        }
    } else {
        cart[restaurant_id][product_id] += update;
    }
    render_site();
}
async function place_order() {
    const order = Object.entries(cart).map(([restaurant_id, ordered_products]) => 
        Object.entries(ordered_products).map(([product_id, product_count]) => ({
            rid: restaurant_id,
            id: product_id,
            count: product_count
        }))).flat();
    response = await make_json_request("/customer_rq/place_order", {
        access_token: access_token,
        order: order
    });
    if(!response.ok) {
        throw new Error("TBD: failed to place order... Some sort of error stack would be nice");
    }
    // TODO: error stack again, used for displaying "Successfully placed in order"
    // empty the cart
    cart = {};
    render_state = CUSTOMER_RENDER_STATE_MENU;
    render_site();
}
async function accept_orders() {
    pending_orders = Array.from(document.querySelectorAll('input[name="pending_order"]:checked')).map(order_cb => parseInt(order_cb.value));
    if(pending_orders.length > 0) {
        console.log(pending_orders);
        response = await make_json_request("/delivery_rq/accept_orders", {
            access_token: access_token,
            orders: pending_orders
        });
        if(!response.ok) {
            throw new Error("TBD: failed to accept orders... Some sort of error stack would be nice");
        }
        render_state = CUSTOMER_RENDER_STATE_MENU;
        render_site();
    }
}
async function render_site() {
    assert_non_null(access_token, "Called render_site before initialising access_token");
    let body = "";
    let whatever = assert_non_null(document.getElementById("render-area"));
    switch (account_info.type) {
    case "Admin": {
        const buttons = [];
        buttons[ADMIN_RENDER_STATE_ACCOUNTS] = "Accounts";
        buttons[ADMIN_RENDER_STATE_RESTAURANTS] = "Restaurants";
        buttons[ADMIN_RENDER_STATE_PRODUCTS] = "Products";
        body += buttons.map(button_for_render_state).join("");
        body += `<br>`;
        switch(render_state) {
        case ADMIN_RENDER_STATE_ACCOUNTS:
            const accounts = await (await admin_request_get_accounts(access_token)).json();
            assert_non_null(accounts[0]);
            body += table_from_object_array(accounts.map((res, index) => ({
                id: index,
                ...res,
            })));
            break;
        case ADMIN_RENDER_STATE_RESTAURANTS:
            const restaurants = await (await request_get_restaurants(access_token)).json();
            assert_non_null(restaurants[0]);
            body += table_from_object_array(restaurants.map((res, index) => ({
                id: index,
                ...res,
            })));
            break;
        case ADMIN_RENDER_STATE_PRODUCTS:
            const restaurant_box = document.getElementById("restaurant_id");
            body += `<input id="restaurant_id" type="number" placeholder="Restaurant ID" onkeydown="if(event.key == 'Enter') render_site()">`;
            if(restaurant_box != null && restaurant_box.value != "") {
                const restaurant_id = parseInt(restaurant_box.value);
                const products_req = await request_get_products(access_token, restaurant_id);
                if(!products_req.ok) {
                    restaurant_box.value = null;
                    body += `<h2>Invalid restaurant id ${restaurant_id}</h2>`;
                    break;
                }
                const products = await products_req.json();
                assert_non_null(products[0]);
                body += table_from_object_array(products.map((res, index) => ({
                    id: index,
                    ...res,
                })));
            }
            break;
        default:
            throw new Error("TODO: Unhandled admin render state" + render_state);
            break;
        }
    } break;
    case "Customer": {
        const buttons = [];
        buttons[CUSTOMER_RENDER_STATE_MENU] = "Menu";
        const cart_count = Object.values(cart)
            .reduce((accum, products) => accum + Object.entries(products).length, 0)
        buttons[CUSTOMER_RENDER_STATE_CART] = cart_count ? `Cart (${cart_count})` : `Cart`;
        body += buttons.map(button_for_render_state).join("");
        body += `<br>`;
        switch(render_state) {
        case CUSTOMER_RENDER_STATE_CART:
            if(cart_count == 0) {
                body += `Oops. Cart is empty :)`
            } else {
                // TODO: cache
                const restaurants = (await (await request_get_restaurants(access_token)).json()).map((res, index) => ({
                    id: index,
                    ...res
                }));
                const restaurant_product_pair = await Promise.all(restaurants.map(async (res) => ({
                    products: (await (await request_get_products(access_token, res.id)).json()),
                    ...res
                })));
                price = 0;
                body += `<div class="cart-container">`
                for(const restaurant_id in cart) {
                    for(const product_id in cart[restaurant_id]) {
                        const restaurant = restaurant_product_pair[restaurant_id];
                        const product = restaurant.products[product_id];
                        body += `
                            <span class="cart-name"> ${product.name} </span>
                            <div></div>
                            <div class="cart-buttons">
                                <button onclick="cart_update_count(${restaurant_id}, ${product_id}, -1)">-</button>
                                <span class="cart-counter"> ${cart[restaurant_id][product_id]} </span>
                                <button onclick="cart_update_count(${restaurant_id}, ${product_id}, 1)">+</button>
                            </div>
                            <span class="cart-price"> €${product.price} </span>
                        `;
                        price += product.price * cart[restaurant_id][product_id];
                    }
                }
                body += `</div>`
                body += 
                `<div class="cart-container"> 
                    <span class="cart-total"> Total: </span>
                    <div></div>
                    <span class="cart-price"> €${price} </span>
                </div>`
                body +=`<br>`
                body += 
                `<button class="order-button" onclick="place_order()">
                    Order now
                </button>`
            }
            break;
        case CUSTOMER_RENDER_STATE_MENU: {
            // TODO: cache
            const restaurants = (await (await request_get_restaurants(access_token)).json()).map((res, index) => ({
                id: index,
                ...res
            }));
            const restaurant_product_pair = await Promise.all(restaurants.map(async (res) => ({
                products: (await (await request_get_products(access_token, res.id)).json()),
                ...res
            })));
            body += restaurant_product_pair.map((restaurant, idx) => product_feature_from_array(idx, restaurant.name, restaurant.products)).join("");
        } break;
        }
        // body += product_feature_from_array("Whatever", [ "Foo", "Bar", "Baz" ]);
        // throw new Error("TODO: Handle Customer");
    } break;
    case "Manager":
        throw new Error("TODO: Handle RestaurantOwner");
        break;
    case "DeliveryGuy":
        switch(render_state) {
        case DELIVERY_RENDER_STATE_ORDERS:
            const orders = await (await delivery_get_pending_orders(access_token)).json();
            body += orders.map((order) => 
                `<input type="checkbox" name="pending_order" value=${order.id}>
                <span> Order number #${order.id}</span>
                <ul>${order.order_items.map((item) => `
                    <li>${item.count} Product ${item.id} from restaurant ${item.rid}
                `).join("")}
                </ul>
                `).join("<br>");

            body += `<button class="order-button" onclick="accept_orders()"> Accept Order(s) </button>`
            // throw new Error("TODO: get orders " + JSON.stringify(orders));
            break;
        }
        break;
    default:
        throw new Error("unreachable account type " + account_info.type);
    }
    whatever.innerHTML = body;
}
(async () => {
    access_token = get_cookie("access-token");

    if (access_token == null) {
        send_to_login_page();
    } else {
        document.getElementById("foo").innerHTML += access_token;
    }
    const account_info_req = await make_account_info_request(access_token);
    if (!account_info_req.ok) {
        send_to_login_page();
    }
    account_info = await account_info_req.json();
    await render_site()
})();
