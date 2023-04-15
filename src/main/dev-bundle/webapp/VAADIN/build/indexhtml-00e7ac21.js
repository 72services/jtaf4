(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const n of document.querySelectorAll('link[rel="modulepreload"]'))i(n);new MutationObserver(n=>{for(const r of n)if(r.type==="childList")for(const s of r.addedNodes)s.tagName==="LINK"&&s.rel==="modulepreload"&&i(s)}).observe(document,{childList:!0,subtree:!0});function t(n){const r={};return n.integrity&&(r.integrity=n.integrity),n.referrerPolicy&&(r.referrerPolicy=n.referrerPolicy),n.crossOrigin==="use-credentials"?r.credentials="include":n.crossOrigin==="anonymous"?r.credentials="omit":r.credentials="same-origin",r}function i(n){if(n.ep)return;n.ep=!0;const r=t(n);fetch(n.href,r)}})();window.Vaadin=window.Vaadin||{};window.Vaadin.featureFlags=window.Vaadin.featureFlags||{};window.Vaadin.featureFlags.exampleFeatureFlag=!1;window.Vaadin.featureFlags.collaborationEngineBackend=!1;const mi="modulepreload",hi=function(o,e){return new URL(o,e).href},Yt={},He=function(e,t,i){if(!t||t.length===0)return e();const n=document.getElementsByTagName("link");return Promise.all(t.map(r=>{if(r=hi(r,i),r in Yt)return;Yt[r]=!0;const s=r.endsWith(".css"),l=s?'[rel="stylesheet"]':"";if(!!i)for(let u=n.length-1;u>=0;u--){const p=n[u];if(p.href===r&&(!s||p.rel==="stylesheet"))return}else if(document.querySelector(`link[href="${r}"]${l}`))return;const c=document.createElement("link");if(c.rel=s?"stylesheet":mi,s||(c.as="script",c.crossOrigin=""),c.href=r,document.head.appendChild(c),s)return new Promise((u,p)=>{c.addEventListener("load",u),c.addEventListener("error",()=>p(new Error(`Unable to preload CSS for ${r}`)))})})).then(()=>e())};function je(o){return o=o||[],Array.isArray(o)?o:[o]}function D(o){return`[Vaadin.Router] ${o}`}function pi(o){if(typeof o!="object")return String(o);const e=Object.prototype.toString.call(o).match(/ (.*)\]$/)[1];return e==="Object"||e==="Array"?`${e} ${JSON.stringify(o)}`:e}const We="module",Ge="nomodule",Tt=[We,Ge];function Jt(o){if(!o.match(/.+\.[m]?js$/))throw new Error(D(`Unsupported type for bundle "${o}": .js or .mjs expected.`))}function $o(o){if(!o||!M(o.path))throw new Error(D('Expected route config to be an object with a "path" string property, or an array of such objects'));const e=o.bundle,t=["component","redirect","bundle"];if(!Q(o.action)&&!Array.isArray(o.children)&&!Q(o.children)&&!qe(e)&&!t.some(i=>M(o[i])))throw new Error(D(`Expected route config "${o.path}" to include either "${t.join('", "')}" or "action" function but none found.`));if(e)if(M(e))Jt(e);else if(Tt.some(i=>i in e))Tt.forEach(i=>i in e&&Jt(e[i]));else throw new Error(D('Expected route bundle to include either "'+Ge+'" or "'+We+'" keys, or both'));o.redirect&&["bundle","component"].forEach(i=>{i in o&&console.warn(D(`Route config "${o.path}" has both "redirect" and "${i}" properties, and "redirect" will always override the latter. Did you mean to only use "${i}"?`))})}function Xt(o){je(o).forEach(e=>$o(e))}function Qt(o,e){let t=document.head.querySelector('script[src="'+o+'"][async]');return t||(t=document.createElement("script"),t.setAttribute("src",o),e===We?t.setAttribute("type",We):e===Ge&&t.setAttribute(Ge,""),t.async=!0),new Promise((i,n)=>{t.onreadystatechange=t.onload=r=>{t.__dynamicImportLoaded=!0,i(r)},t.onerror=r=>{t.parentNode&&t.parentNode.removeChild(t),n(r)},t.parentNode===null?document.head.appendChild(t):t.__dynamicImportLoaded&&i()})}function fi(o){return M(o)?Qt(o):Promise.race(Tt.filter(e=>e in o).map(e=>Qt(o[e],e)))}function be(o,e){return!window.dispatchEvent(new CustomEvent(`vaadin-router-${o}`,{cancelable:o==="go",detail:e}))}function qe(o){return typeof o=="object"&&!!o}function Q(o){return typeof o=="function"}function M(o){return typeof o=="string"}function ko(o){const e=new Error(D(`Page not found (${o.pathname})`));return e.context=o,e.code=404,e}const le=new class{};function gi(o){const e=o.port,t=o.protocol,r=t==="http:"&&e==="80"||t==="https:"&&e==="443"?o.hostname:o.host;return`${t}//${r}`}function Zt(o){if(o.defaultPrevented||o.button!==0||o.shiftKey||o.ctrlKey||o.altKey||o.metaKey)return;let e=o.target;const t=o.composedPath?o.composedPath():o.path||[];for(let l=0;l<t.length;l++){const a=t[l];if(a.nodeName&&a.nodeName.toLowerCase()==="a"){e=a;break}}for(;e&&e.nodeName.toLowerCase()!=="a";)e=e.parentNode;if(!e||e.nodeName.toLowerCase()!=="a"||e.target&&e.target.toLowerCase()!=="_self"||e.hasAttribute("download")||e.hasAttribute("router-ignore")||e.pathname===window.location.pathname&&e.hash!==""||(e.origin||gi(e))!==window.location.origin)return;const{pathname:n,search:r,hash:s}=e;be("go",{pathname:n,search:r,hash:s})&&(o.preventDefault(),o&&o.type==="click"&&window.scrollTo(0,0))}const vi={activate(){window.document.addEventListener("click",Zt)},inactivate(){window.document.removeEventListener("click",Zt)}},yi=/Trident/.test(navigator.userAgent);yi&&!Q(window.PopStateEvent)&&(window.PopStateEvent=function(o,e){e=e||{};var t=document.createEvent("Event");return t.initEvent(o,Boolean(e.bubbles),Boolean(e.cancelable)),t.state=e.state||null,t},window.PopStateEvent.prototype=window.Event.prototype);function eo(o){if(o.state==="vaadin-router-ignore")return;const{pathname:e,search:t,hash:i}=window.location;be("go",{pathname:e,search:t,hash:i})}const bi={activate(){window.addEventListener("popstate",eo)},inactivate(){window.removeEventListener("popstate",eo)}};var pe=Po,xi=At,wi=Ci,_i=Lo,Si=Ao,Ro="/",No="./",Ei=new RegExp(["(\\\\.)","(?:\\:(\\w+)(?:\\(((?:\\\\.|[^\\\\()])+)\\))?|\\(((?:\\\\.|[^\\\\()])+)\\))([+*?])?"].join("|"),"g");function At(o,e){for(var t=[],i=0,n=0,r="",s=e&&e.delimiter||Ro,l=e&&e.delimiters||No,a=!1,c;(c=Ei.exec(o))!==null;){var u=c[0],p=c[1],h=c.index;if(r+=o.slice(n,h),n=h+u.length,p){r+=p[1],a=!0;continue}var f="",G=o[n],q=c[2],H=c[3],Ze=c[4],A=c[5];if(!a&&r.length){var V=r.length-1;l.indexOf(r[V])>-1&&(f=r[V],r=r.slice(0,V))}r&&(t.push(r),r="",a=!1);var oe=f!==""&&G!==void 0&&G!==f,ie=A==="+"||A==="*",et=A==="?"||A==="*",j=f||s,Re=H||Ze;t.push({name:q||i++,prefix:f,delimiter:j,optional:et,repeat:ie,partial:oe,pattern:Re?Ti(Re):"[^"+K(j)+"]+?"})}return(r||n<o.length)&&t.push(r+o.substr(n)),t}function Ci(o,e){return Lo(At(o,e))}function Lo(o){for(var e=new Array(o.length),t=0;t<o.length;t++)typeof o[t]=="object"&&(e[t]=new RegExp("^(?:"+o[t].pattern+")$"));return function(i,n){for(var r="",s=n&&n.encode||encodeURIComponent,l=0;l<o.length;l++){var a=o[l];if(typeof a=="string"){r+=a;continue}var c=i?i[a.name]:void 0,u;if(Array.isArray(c)){if(!a.repeat)throw new TypeError('Expected "'+a.name+'" to not repeat, but got array');if(c.length===0){if(a.optional)continue;throw new TypeError('Expected "'+a.name+'" to not be empty')}for(var p=0;p<c.length;p++){if(u=s(c[p],a),!e[l].test(u))throw new TypeError('Expected all "'+a.name+'" to match "'+a.pattern+'"');r+=(p===0?a.prefix:a.delimiter)+u}continue}if(typeof c=="string"||typeof c=="number"||typeof c=="boolean"){if(u=s(String(c),a),!e[l].test(u))throw new TypeError('Expected "'+a.name+'" to match "'+a.pattern+'", but got "'+u+'"');r+=a.prefix+u;continue}if(a.optional){a.partial&&(r+=a.prefix);continue}throw new TypeError('Expected "'+a.name+'" to be '+(a.repeat?"an array":"a string"))}return r}}function K(o){return o.replace(/([.+*?=^!:${}()[\]|/\\])/g,"\\$1")}function Ti(o){return o.replace(/([=!:$/()])/g,"\\$1")}function Io(o){return o&&o.sensitive?"":"i"}function $i(o,e){if(!e)return o;var t=o.source.match(/\((?!\?)/g);if(t)for(var i=0;i<t.length;i++)e.push({name:i,prefix:null,delimiter:null,optional:!1,repeat:!1,partial:!1,pattern:null});return o}function ki(o,e,t){for(var i=[],n=0;n<o.length;n++)i.push(Po(o[n],e,t).source);return new RegExp("(?:"+i.join("|")+")",Io(t))}function Ri(o,e,t){return Ao(At(o,t),e,t)}function Ao(o,e,t){t=t||{};for(var i=t.strict,n=t.start!==!1,r=t.end!==!1,s=K(t.delimiter||Ro),l=t.delimiters||No,a=[].concat(t.endsWith||[]).map(K).concat("$").join("|"),c=n?"^":"",u=o.length===0,p=0;p<o.length;p++){var h=o[p];if(typeof h=="string")c+=K(h),u=p===o.length-1&&l.indexOf(h[h.length-1])>-1;else{var f=h.repeat?"(?:"+h.pattern+")(?:"+K(h.delimiter)+"(?:"+h.pattern+"))*":h.pattern;e&&e.push(h),h.optional?h.partial?c+=K(h.prefix)+"("+f+")?":c+="(?:"+K(h.prefix)+"("+f+"))?":c+=K(h.prefix)+"("+f+")"}}return r?(i||(c+="(?:"+s+")?"),c+=a==="$"?"$":"(?="+a+")"):(i||(c+="(?:"+s+"(?="+a+"))?"),u||(c+="(?="+s+"|"+a+")")),new RegExp(c,Io(t))}function Po(o,e,t){return o instanceof RegExp?$i(o,e):Array.isArray(o)?ki(o,e,t):Ri(o,e,t)}pe.parse=xi;pe.compile=wi;pe.tokensToFunction=_i;pe.tokensToRegExp=Si;const{hasOwnProperty:Ni}=Object.prototype,$t=new Map;$t.set("|false",{keys:[],pattern:/(?:)/});function to(o){try{return decodeURIComponent(o)}catch{return o}}function Li(o,e,t,i,n){t=!!t;const r=`${o}|${t}`;let s=$t.get(r);if(!s){const c=[];s={keys:c,pattern:pe(o,c,{end:t,strict:o===""})},$t.set(r,s)}const l=s.pattern.exec(e);if(!l)return null;const a=Object.assign({},n);for(let c=1;c<l.length;c++){const u=s.keys[c-1],p=u.name,h=l[c];(h!==void 0||!Ni.call(a,p))&&(u.repeat?a[p]=h?h.split(u.delimiter).map(to):[]:a[p]=h&&to(h))}return{path:l[0],keys:(i||[]).concat(s.keys),params:a}}function Oo(o,e,t,i,n){let r,s,l=0,a=o.path||"";return a.charAt(0)==="/"&&(t&&(a=a.substr(1)),t=!0),{next(c){if(o===c)return{done:!0};const u=o.__children=o.__children||o.children;if(!r&&(r=Li(a,e,!u,i,n),r))return{done:!1,value:{route:o,keys:r.keys,params:r.params,path:r.path}};if(r&&u)for(;l<u.length;){if(!s){const h=u[l];h.parent=o;let f=r.path.length;f>0&&e.charAt(f)==="/"&&(f+=1),s=Oo(h,e.substr(f),t,r.keys,r.params)}const p=s.next(c);if(!p.done)return{done:!1,value:p.value};s=null,l++}return{done:!0}}}}function Ii(o){if(Q(o.route.action))return o.route.action(o)}function Ai(o,e){let t=e;for(;t;)if(t=t.parent,t===o)return!0;return!1}function Pi(o){let e=`Path '${o.pathname}' is not properly resolved due to an error.`;const t=(o.route||{}).path;return t&&(e+=` Resolution had failed on route: '${t}'`),e}function Oi(o,e){const{route:t,path:i}=e;if(t&&!t.__synthetic){const n={path:i,route:t};if(!o.chain)o.chain=[];else if(t.parent){let r=o.chain.length;for(;r--&&o.chain[r].route&&o.chain[r].route!==t.parent;)o.chain.pop()}o.chain.push(n)}}class we{constructor(e,t={}){if(Object(e)!==e)throw new TypeError("Invalid routes");this.baseUrl=t.baseUrl||"",this.errorHandler=t.errorHandler,this.resolveRoute=t.resolveRoute||Ii,this.context=Object.assign({resolver:this},t.context),this.root=Array.isArray(e)?{path:"",__children:e,parent:null,__synthetic:!0}:e,this.root.parent=null}getRoutes(){return[...this.root.__children]}setRoutes(e){Xt(e);const t=[...je(e)];this.root.__children=t}addRoutes(e){return Xt(e),this.root.__children.push(...je(e)),this.getRoutes()}removeRoutes(){this.setRoutes([])}resolve(e){const t=Object.assign({},this.context,M(e)?{pathname:e}:e),i=Oo(this.root,this.__normalizePathname(t.pathname),this.baseUrl),n=this.resolveRoute;let r=null,s=null,l=t;function a(c,u=r.value.route,p){const h=p===null&&r.value.route;return r=s||i.next(h),s=null,!c&&(r.done||!Ai(u,r.value.route))?(s=r,Promise.resolve(le)):r.done?Promise.reject(ko(t)):(l=Object.assign(l?{chain:l.chain?l.chain.slice(0):[]}:{},t,r.value),Oi(l,r.value),Promise.resolve(n(l)).then(f=>f!=null&&f!==le?(l.result=f.result||f,l):a(c,u,f)))}return t.next=a,Promise.resolve().then(()=>a(!0,this.root)).catch(c=>{const u=Pi(l);if(c?console.warn(u):c=new Error(u),c.context=c.context||l,c instanceof DOMException||(c.code=c.code||500),this.errorHandler)return l.result=this.errorHandler(c),l;throw c})}static __createUrl(e,t){return new URL(e,t)}get __effectiveBaseUrl(){return this.baseUrl?this.constructor.__createUrl(this.baseUrl,document.baseURI||document.URL).href.replace(/[^\/]*$/,""):""}__normalizePathname(e){if(!this.baseUrl)return e;const t=this.__effectiveBaseUrl,i=this.constructor.__createUrl(e,t).href;if(i.slice(0,t.length)===t)return i.slice(t.length)}}we.pathToRegexp=pe;const{pathToRegexp:oo}=we,io=new Map;function zo(o,e,t){const i=e.name||e.component;if(i&&(o.has(i)?o.get(i).push(e):o.set(i,[e])),Array.isArray(t))for(let n=0;n<t.length;n++){const r=t[n];r.parent=e,zo(o,r,r.__children||r.children)}}function no(o,e){const t=o.get(e);if(t&&t.length>1)throw new Error(`Duplicate route with name "${e}". Try seting unique 'name' route properties.`);return t&&t[0]}function ro(o){let e=o.path;return e=Array.isArray(e)?e[0]:e,e!==void 0?e:""}function zi(o,e={}){if(!(o instanceof we))throw new TypeError("An instance of Resolver is expected");const t=new Map;return(i,n)=>{let r=no(t,i);if(!r&&(t.clear(),zo(t,o.root,o.root.__children),r=no(t,i),!r))throw new Error(`Route "${i}" not found`);let s=io.get(r.fullPath);if(!s){let a=ro(r),c=r.parent;for(;c;){const f=ro(c);f&&(a=f.replace(/\/$/,"")+"/"+a.replace(/^\//,"")),c=c.parent}const u=oo.parse(a),p=oo.tokensToFunction(u),h=Object.create(null);for(let f=0;f<u.length;f++)M(u[f])||(h[u[f].name]=!0);s={toPath:p,keys:h},io.set(a,s),r.fullPath=a}let l=s.toPath(n,e)||"/";if(e.stringifyQueryParams&&n){const a={},c=Object.keys(n);for(let p=0;p<c.length;p++){const h=c[p];s.keys[h]||(a[h]=n[h])}const u=e.stringifyQueryParams(a);u&&(l+=u.charAt(0)==="?"?u:`?${u}`)}return l}}let so=[];function Mi(o){so.forEach(e=>e.inactivate()),o.forEach(e=>e.activate()),so=o}const Di=o=>{const e=getComputedStyle(o).getPropertyValue("animation-name");return e&&e!=="none"},Vi=(o,e)=>{const t=()=>{o.removeEventListener("animationend",t),e()};o.addEventListener("animationend",t)};function ao(o,e){return o.classList.add(e),new Promise(t=>{if(Di(o)){const i=o.getBoundingClientRect(),n=`height: ${i.bottom-i.top}px; width: ${i.right-i.left}px`;o.setAttribute("style",`position: absolute; ${n}`),Vi(o,()=>{o.classList.remove(e),o.removeAttribute("style"),t()})}else o.classList.remove(e),t()})}const Ui=256;function nt(o){return o!=null}function Fi(o){const e=Object.assign({},o);return delete e.next,e}function O({pathname:o="",search:e="",hash:t="",chain:i=[],params:n={},redirectFrom:r,resolver:s},l){const a=i.map(c=>c.route);return{baseUrl:s&&s.baseUrl||"",pathname:o,search:e,hash:t,routes:a,route:l||a.length&&a[a.length-1]||null,params:n,redirectFrom:r,getUrl:(c={})=>Me(Y.pathToRegexp.compile(Mo(a))(Object.assign({},n,c)),s)}}function lo(o,e){const t=Object.assign({},o.params);return{redirect:{pathname:e,from:o.pathname,params:t}}}function Bi(o,e){e.location=O(o);const t=o.chain.map(i=>i.route).indexOf(o.route);return o.chain[t].element=e,e}function ze(o,e,t){if(Q(o))return o.apply(t,e)}function co(o,e,t){return i=>{if(i&&(i.cancel||i.redirect))return i;if(t)return ze(t[o],e,t)}}function Hi(o,e){if(!Array.isArray(o)&&!qe(o))throw new Error(D(`Incorrect "children" value for the route ${e.path}: expected array or object, but got ${o}`));e.__children=[];const t=je(o);for(let i=0;i<t.length;i++)$o(t[i]),e.__children.push(t[i])}function Pe(o){if(o&&o.length){const e=o[0].parentNode;for(let t=0;t<o.length;t++)e.removeChild(o[t])}}function Me(o,e){const t=e.__effectiveBaseUrl;return t?e.constructor.__createUrl(o.replace(/^\//,""),t).pathname:o}function Mo(o){return o.map(e=>e.path).reduce((e,t)=>t.length?e.replace(/\/$/,"")+"/"+t.replace(/^\//,""):e,"")}class Y extends we{constructor(e,t){const i=document.head.querySelector("base"),n=i&&i.getAttribute("href");super([],Object.assign({baseUrl:n&&we.__createUrl(n,document.URL).pathname.replace(/[^\/]*$/,"")},t)),this.resolveRoute=s=>this.__resolveRoute(s);const r=Y.NavigationTrigger;Y.setTriggers.apply(Y,Object.keys(r).map(s=>r[s])),this.baseUrl,this.ready,this.ready=Promise.resolve(e),this.location,this.location=O({resolver:this}),this.__lastStartedRenderId=0,this.__navigationEventHandler=this.__onNavigationEvent.bind(this),this.setOutlet(e),this.subscribe(),this.__createdByRouter=new WeakMap,this.__addedByRouter=new WeakMap}__resolveRoute(e){const t=e.route;let i=Promise.resolve();Q(t.children)&&(i=i.then(()=>t.children(Fi(e))).then(r=>{!nt(r)&&!Q(t.children)&&(r=t.children),Hi(r,t)}));const n={redirect:r=>lo(e,r),component:r=>{const s=document.createElement(r);return this.__createdByRouter.set(s,!0),s}};return i.then(()=>{if(this.__isLatestRender(e))return ze(t.action,[e,n],t)}).then(r=>{if(nt(r)&&(r instanceof HTMLElement||r.redirect||r===le))return r;if(M(t.redirect))return n.redirect(t.redirect);if(t.bundle)return fi(t.bundle).then(()=>{},()=>{throw new Error(D(`Bundle not found: ${t.bundle}. Check if the file name is correct`))})}).then(r=>{if(nt(r))return r;if(M(t.component))return n.component(t.component)})}setOutlet(e){e&&this.__ensureOutlet(e),this.__outlet=e}getOutlet(){return this.__outlet}setRoutes(e,t=!1){return this.__previousContext=void 0,this.__urlForName=void 0,super.setRoutes(e),t||this.__onNavigationEvent(),this.ready}render(e,t){const i=++this.__lastStartedRenderId,n=Object.assign({search:"",hash:""},M(e)?{pathname:e}:e,{__renderId:i});return this.ready=this.resolve(n).then(r=>this.__fullyResolveChain(r)).then(r=>{if(this.__isLatestRender(r)){const s=this.__previousContext;if(r===s)return this.__updateBrowserHistory(s,!0),this.location;if(this.location=O(r),t&&this.__updateBrowserHistory(r,i===1),be("location-changed",{router:this,location:this.location}),r.__skipAttach)return this.__copyUnchangedElements(r,s),this.__previousContext=r,this.location;this.__addAppearingContent(r,s);const l=this.__animateIfNeeded(r);return this.__runOnAfterEnterCallbacks(r),this.__runOnAfterLeaveCallbacks(r,s),l.then(()=>{if(this.__isLatestRender(r))return this.__removeDisappearingContent(),this.__previousContext=r,this.location})}}).catch(r=>{if(i===this.__lastStartedRenderId)throw t&&this.__updateBrowserHistory(n),Pe(this.__outlet&&this.__outlet.children),this.location=O(Object.assign(n,{resolver:this})),be("error",Object.assign({router:this,error:r},n)),r}),this.ready}__fullyResolveChain(e,t=e){return this.__findComponentContextAfterAllRedirects(t).then(i=>{const r=i!==t?i:e,l=Me(Mo(i.chain),i.resolver)===i.pathname,a=(c,u=c.route,p)=>c.next(void 0,u,p).then(h=>h===null||h===le?l?c:u.parent!==null?a(c,u.parent,h):h:h);return a(i).then(c=>{if(c===null||c===le)throw ko(r);return c&&c!==le&&c!==i?this.__fullyResolveChain(r,c):this.__amendWithOnBeforeCallbacks(i)})})}__findComponentContextAfterAllRedirects(e){const t=e.result;return t instanceof HTMLElement?(Bi(e,t),Promise.resolve(e)):t.redirect?this.__redirect(t.redirect,e.__redirectCount,e.__renderId).then(i=>this.__findComponentContextAfterAllRedirects(i)):t instanceof Error?Promise.reject(t):Promise.reject(new Error(D(`Invalid route resolution result for path "${e.pathname}". Expected redirect object or HTML element, but got: "${pi(t)}". Double check the action return value for the route.`)))}__amendWithOnBeforeCallbacks(e){return this.__runOnBeforeCallbacks(e).then(t=>t===this.__previousContext||t===e?t:this.__fullyResolveChain(t))}__runOnBeforeCallbacks(e){const t=this.__previousContext||{},i=t.chain||[],n=e.chain;let r=Promise.resolve();const s=()=>({cancel:!0}),l=a=>lo(e,a);if(e.__divergedChainIndex=0,e.__skipAttach=!1,i.length){for(let a=0;a<Math.min(i.length,n.length)&&!(i[a].route!==n[a].route||i[a].path!==n[a].path&&i[a].element!==n[a].element||!this.__isReusableElement(i[a].element,n[a].element));a=++e.__divergedChainIndex);if(e.__skipAttach=n.length===i.length&&e.__divergedChainIndex==n.length&&this.__isReusableElement(e.result,t.result),e.__skipAttach){for(let a=n.length-1;a>=0;a--)r=this.__runOnBeforeLeaveCallbacks(r,e,{prevent:s},i[a]);for(let a=0;a<n.length;a++)r=this.__runOnBeforeEnterCallbacks(r,e,{prevent:s,redirect:l},n[a]),i[a].element.location=O(e,i[a].route)}else for(let a=i.length-1;a>=e.__divergedChainIndex;a--)r=this.__runOnBeforeLeaveCallbacks(r,e,{prevent:s},i[a])}if(!e.__skipAttach)for(let a=0;a<n.length;a++)a<e.__divergedChainIndex?a<i.length&&i[a].element&&(i[a].element.location=O(e,i[a].route)):(r=this.__runOnBeforeEnterCallbacks(r,e,{prevent:s,redirect:l},n[a]),n[a].element&&(n[a].element.location=O(e,n[a].route)));return r.then(a=>{if(a){if(a.cancel)return this.__previousContext.__renderId=e.__renderId,this.__previousContext;if(a.redirect)return this.__redirect(a.redirect,e.__redirectCount,e.__renderId)}return e})}__runOnBeforeLeaveCallbacks(e,t,i,n){const r=O(t);return e.then(s=>{if(this.__isLatestRender(t))return co("onBeforeLeave",[r,i,this],n.element)(s)}).then(s=>{if(!(s||{}).redirect)return s})}__runOnBeforeEnterCallbacks(e,t,i,n){const r=O(t,n.route);return e.then(s=>{if(this.__isLatestRender(t))return co("onBeforeEnter",[r,i,this],n.element)(s)})}__isReusableElement(e,t){return e&&t?this.__createdByRouter.get(e)&&this.__createdByRouter.get(t)?e.localName===t.localName:e===t:!1}__isLatestRender(e){return e.__renderId===this.__lastStartedRenderId}__redirect(e,t,i){if(t>Ui)throw new Error(D(`Too many redirects when rendering ${e.from}`));return this.resolve({pathname:this.urlForPath(e.pathname,e.params),redirectFrom:e.from,__redirectCount:(t||0)+1,__renderId:i})}__ensureOutlet(e=this.__outlet){if(!(e instanceof Node))throw new TypeError(D(`Expected router outlet to be a valid DOM Node (but got ${e})`))}__updateBrowserHistory({pathname:e,search:t="",hash:i=""},n){if(window.location.pathname!==e||window.location.search!==t||window.location.hash!==i){const r=n?"replaceState":"pushState";window.history[r](null,document.title,e+t+i),window.dispatchEvent(new PopStateEvent("popstate",{state:"vaadin-router-ignore"}))}}__copyUnchangedElements(e,t){let i=this.__outlet;for(let n=0;n<e.__divergedChainIndex;n++){const r=t&&t.chain[n].element;if(r)if(r.parentNode===i)e.chain[n].element=r,i=r;else break}return i}__addAppearingContent(e,t){this.__ensureOutlet(),this.__removeAppearingContent();const i=this.__copyUnchangedElements(e,t);this.__appearingContent=[],this.__disappearingContent=Array.from(i.children).filter(r=>this.__addedByRouter.get(r)&&r!==e.result);let n=i;for(let r=e.__divergedChainIndex;r<e.chain.length;r++){const s=e.chain[r].element;s&&(n.appendChild(s),this.__addedByRouter.set(s,!0),n===i&&this.__appearingContent.push(s),n=s)}}__removeDisappearingContent(){this.__disappearingContent&&Pe(this.__disappearingContent),this.__disappearingContent=null,this.__appearingContent=null}__removeAppearingContent(){this.__disappearingContent&&this.__appearingContent&&(Pe(this.__appearingContent),this.__disappearingContent=null,this.__appearingContent=null)}__runOnAfterLeaveCallbacks(e,t){if(t)for(let i=t.chain.length-1;i>=e.__divergedChainIndex&&this.__isLatestRender(e);i--){const n=t.chain[i].element;if(n)try{const r=O(e);ze(n.onAfterLeave,[r,{},t.resolver],n)}finally{this.__disappearingContent.indexOf(n)>-1&&Pe(n.children)}}}__runOnAfterEnterCallbacks(e){for(let t=e.__divergedChainIndex;t<e.chain.length&&this.__isLatestRender(e);t++){const i=e.chain[t].element||{},n=O(e,e.chain[t].route);ze(i.onAfterEnter,[n,{},e.resolver],i)}}__animateIfNeeded(e){const t=(this.__disappearingContent||[])[0],i=(this.__appearingContent||[])[0],n=[],r=e.chain;let s;for(let l=r.length;l>0;l--)if(r[l-1].route.animate){s=r[l-1].route.animate;break}if(t&&i&&s){const l=qe(s)&&s.leave||"leaving",a=qe(s)&&s.enter||"entering";n.push(ao(t,l)),n.push(ao(i,a))}return Promise.all(n).then(()=>e)}subscribe(){window.addEventListener("vaadin-router-go",this.__navigationEventHandler)}unsubscribe(){window.removeEventListener("vaadin-router-go",this.__navigationEventHandler)}__onNavigationEvent(e){const{pathname:t,search:i,hash:n}=e?e.detail:window.location;M(this.__normalizePathname(t))&&(e&&e.preventDefault&&e.preventDefault(),this.render({pathname:t,search:i,hash:n},!0))}static setTriggers(...e){Mi(e)}urlForName(e,t){return this.__urlForName||(this.__urlForName=zi(this)),Me(this.__urlForName(e,t),this)}urlForPath(e,t){return Me(Y.pathToRegexp.compile(e)(t),this)}static go(e){const{pathname:t,search:i,hash:n}=M(e)?this.__createUrl(e,"http://a"):e;return be("go",{pathname:t,search:i,hash:n})}}const ji=/\/\*[\*!]\s+vaadin-dev-mode:start([\s\S]*)vaadin-dev-mode:end\s+\*\*\//i,De=window.Vaadin&&window.Vaadin.Flow&&window.Vaadin.Flow.clients;function Wi(){function o(){return!0}return Do(o)}function Gi(){try{return qi()?!0:Ki()?De?!Yi():!Wi():!1}catch{return!1}}function qi(){return localStorage.getItem("vaadin.developmentmode.force")}function Ki(){return["localhost","127.0.0.1"].indexOf(window.location.hostname)>=0}function Yi(){return!!(De&&Object.keys(De).map(e=>De[e]).filter(e=>e.productionMode).length>0)}function Do(o,e){if(typeof o!="function")return;const t=ji.exec(o.toString());if(t)try{o=new Function(t[1])}catch(i){console.log("vaadin-development-mode-detector: uncommentAndRun() failed",i)}return o(e)}window.Vaadin=window.Vaadin||{};const uo=function(o,e){if(window.Vaadin.developmentMode)return Do(o,e)};window.Vaadin.developmentMode===void 0&&(window.Vaadin.developmentMode=Gi());function Ji(){}const Xi=function(){if(typeof uo=="function")return uo(Ji)};window.Vaadin=window.Vaadin||{};window.Vaadin.registrations=window.Vaadin.registrations||[];window.Vaadin.registrations.push({is:"@vaadin/router",version:"1.7.4"});Xi();Y.NavigationTrigger={POPSTATE:bi,CLICK:vi};var rt,E;(function(o){o.CONNECTED="connected",o.LOADING="loading",o.RECONNECTING="reconnecting",o.CONNECTION_LOST="connection-lost"})(E||(E={}));class Qi{constructor(e){this.stateChangeListeners=new Set,this.loadingCount=0,this.connectionState=e,this.serviceWorkerMessageListener=this.serviceWorkerMessageListener.bind(this),navigator.serviceWorker&&(navigator.serviceWorker.addEventListener("message",this.serviceWorkerMessageListener),navigator.serviceWorker.ready.then(t=>{var i;(i=t==null?void 0:t.active)===null||i===void 0||i.postMessage({method:"Vaadin.ServiceWorker.isConnectionLost",id:"Vaadin.ServiceWorker.isConnectionLost"})}))}addStateChangeListener(e){this.stateChangeListeners.add(e)}removeStateChangeListener(e){this.stateChangeListeners.delete(e)}loadingStarted(){this.state=E.LOADING,this.loadingCount+=1}loadingFinished(){this.decreaseLoadingCount(E.CONNECTED)}loadingFailed(){this.decreaseLoadingCount(E.CONNECTION_LOST)}decreaseLoadingCount(e){this.loadingCount>0&&(this.loadingCount-=1,this.loadingCount===0&&(this.state=e))}get state(){return this.connectionState}set state(e){if(e!==this.connectionState){const t=this.connectionState;this.connectionState=e,this.loadingCount=0;for(const i of this.stateChangeListeners)i(t,this.connectionState)}}get online(){return this.connectionState===E.CONNECTED||this.connectionState===E.LOADING}get offline(){return!this.online}serviceWorkerMessageListener(e){typeof e.data=="object"&&e.data.id==="Vaadin.ServiceWorker.isConnectionLost"&&(e.data.result===!0&&(this.state=E.CONNECTION_LOST),navigator.serviceWorker.removeEventListener("message",this.serviceWorkerMessageListener))}}const Zi=o=>!!(o==="localhost"||o==="[::1]"||o.match(/^127\.\d+\.\d+\.\d+$/)),Oe=window;if(!(!((rt=Oe.Vaadin)===null||rt===void 0)&&rt.connectionState)){let o;Zi(window.location.hostname)?o=!0:o=navigator.onLine,Oe.Vaadin=Oe.Vaadin||{},Oe.Vaadin.connectionState=new Qi(o?E.CONNECTED:E.CONNECTION_LOST)}function I(o,e,t,i){var n=arguments.length,r=n<3?e:i===null?i=Object.getOwnPropertyDescriptor(e,t):i,s;if(typeof Reflect=="object"&&typeof Reflect.decorate=="function")r=Reflect.decorate(o,e,t,i);else for(var l=o.length-1;l>=0;l--)(s=o[l])&&(r=(n<3?s(r):n>3?s(e,t,r):s(e,t))||r);return n>3&&r&&Object.defineProperty(e,t,r),r}/**
 * @license
 * Copyright 2019 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const en=!1,Ve=window,Pt=Ve.ShadowRoot&&(Ve.ShadyCSS===void 0||Ve.ShadyCSS.nativeShadow)&&"adoptedStyleSheets"in Document.prototype&&"replace"in CSSStyleSheet.prototype,Ot=Symbol(),mo=new WeakMap;class zt{constructor(e,t,i){if(this._$cssResult$=!0,i!==Ot)throw new Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");this.cssText=e,this._strings=t}get styleSheet(){let e=this._styleSheet;const t=this._strings;if(Pt&&e===void 0){const i=t!==void 0&&t.length===1;i&&(e=mo.get(t)),e===void 0&&((this._styleSheet=e=new CSSStyleSheet).replaceSync(this.cssText),i&&mo.set(t,e))}return e}toString(){return this.cssText}}const tn=o=>{if(o._$cssResult$===!0)return o.cssText;if(typeof o=="number")return o;throw new Error(`Value passed to 'css' function must be a 'css' function result: ${o}. Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.`)},Vo=o=>new zt(typeof o=="string"?o:String(o),void 0,Ot),b=(o,...e)=>{const t=o.length===1?o[0]:e.reduce((i,n,r)=>i+tn(n)+o[r+1],o[0]);return new zt(t,o,Ot)},on=(o,e)=>{Pt?o.adoptedStyleSheets=e.map(t=>t instanceof CSSStyleSheet?t:t.styleSheet):e.forEach(t=>{const i=document.createElement("style"),n=Ve.litNonce;n!==void 0&&i.setAttribute("nonce",n),i.textContent=t.cssText,o.appendChild(i)})},nn=o=>{let e="";for(const t of o.cssRules)e+=t.cssText;return Vo(e)},ho=Pt||en?o=>o:o=>o instanceof CSSStyleSheet?nn(o):o;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var st,at,lt,Uo;const F=window;let Fo,J;const po=F.trustedTypes,rn=po?po.emptyScript:"",Ue=F.reactiveElementPolyfillSupportDevMode;{const o=(st=F.litIssuedWarnings)!==null&&st!==void 0?st:F.litIssuedWarnings=new Set;J=(e,t)=>{t+=` See https://lit.dev/msg/${e} for more information.`,o.has(t)||(console.warn(t),o.add(t))},J("dev-mode","Lit is in dev mode. Not recommended for production!"),!((at=F.ShadyDOM)===null||at===void 0)&&at.inUse&&Ue===void 0&&J("polyfill-support-missing","Shadow DOM is being polyfilled via `ShadyDOM` but the `polyfill-support` module has not been loaded."),Fo=e=>({then:(t,i)=>{J("request-update-promise",`The \`requestUpdate\` method should no longer return a Promise but does so on \`${e}\`. Use \`updateComplete\` instead.`),t!==void 0&&t(!1)}})}const ct=o=>{F.emitLitDebugLogEvents&&F.dispatchEvent(new CustomEvent("lit-debug",{detail:o}))},Bo=(o,e)=>o,kt={toAttribute(o,e){switch(e){case Boolean:o=o?rn:null;break;case Object:case Array:o=o==null?o:JSON.stringify(o);break}return o},fromAttribute(o,e){let t=o;switch(e){case Boolean:t=o!==null;break;case Number:t=o===null?null:Number(o);break;case Object:case Array:try{t=JSON.parse(o)}catch{t=null}break}return t}},Ho=(o,e)=>e!==o&&(e===e||o===o),dt={attribute:!0,type:String,converter:kt,reflect:!1,hasChanged:Ho},Rt="finalized";class B extends HTMLElement{constructor(){super(),this.__instanceProperties=new Map,this.isUpdatePending=!1,this.hasUpdated=!1,this.__reflectingProperty=null,this._initialize()}static addInitializer(e){var t;this.finalize(),((t=this._initializers)!==null&&t!==void 0?t:this._initializers=[]).push(e)}static get observedAttributes(){this.finalize();const e=[];return this.elementProperties.forEach((t,i)=>{const n=this.__attributeNameForProperty(i,t);n!==void 0&&(this.__attributeToPropertyMap.set(n,i),e.push(n))}),e}static createProperty(e,t=dt){var i;if(t.state&&(t.attribute=!1),this.finalize(),this.elementProperties.set(e,t),!t.noAccessor&&!this.prototype.hasOwnProperty(e)){const n=typeof e=="symbol"?Symbol():`__${e}`,r=this.getPropertyDescriptor(e,n,t);r!==void 0&&(Object.defineProperty(this.prototype,e,r),this.hasOwnProperty("__reactivePropertyKeys")||(this.__reactivePropertyKeys=new Set((i=this.__reactivePropertyKeys)!==null&&i!==void 0?i:[])),this.__reactivePropertyKeys.add(e))}}static getPropertyDescriptor(e,t,i){return{get(){return this[t]},set(n){const r=this[e];this[t]=n,this.requestUpdate(e,r,i)},configurable:!0,enumerable:!0}}static getPropertyOptions(e){return this.elementProperties.get(e)||dt}static finalize(){if(this.hasOwnProperty(Rt))return!1;this[Rt]=!0;const e=Object.getPrototypeOf(this);if(e.finalize(),e._initializers!==void 0&&(this._initializers=[...e._initializers]),this.elementProperties=new Map(e.elementProperties),this.__attributeToPropertyMap=new Map,this.hasOwnProperty(Bo("properties"))){const t=this.properties,i=[...Object.getOwnPropertyNames(t),...Object.getOwnPropertySymbols(t)];for(const n of i)this.createProperty(n,t[n])}this.elementStyles=this.finalizeStyles(this.styles);{const t=(i,n=!1)=>{this.prototype.hasOwnProperty(i)&&J(n?"renamed-api":"removed-api",`\`${i}\` is implemented on class ${this.name}. It has been ${n?"renamed":"removed"} in this version of LitElement.`)};t("initialize"),t("requestUpdateInternal"),t("_getUpdateComplete",!0)}return!0}static finalizeStyles(e){const t=[];if(Array.isArray(e)){const i=new Set(e.flat(1/0).reverse());for(const n of i)t.unshift(ho(n))}else e!==void 0&&t.push(ho(e));return t}static __attributeNameForProperty(e,t){const i=t.attribute;return i===!1?void 0:typeof i=="string"?i:typeof e=="string"?e.toLowerCase():void 0}_initialize(){var e;this.__updatePromise=new Promise(t=>this.enableUpdating=t),this._$changedProperties=new Map,this.__saveInstanceProperties(),this.requestUpdate(),(e=this.constructor._initializers)===null||e===void 0||e.forEach(t=>t(this))}addController(e){var t,i;((t=this.__controllers)!==null&&t!==void 0?t:this.__controllers=[]).push(e),this.renderRoot!==void 0&&this.isConnected&&((i=e.hostConnected)===null||i===void 0||i.call(e))}removeController(e){var t;(t=this.__controllers)===null||t===void 0||t.splice(this.__controllers.indexOf(e)>>>0,1)}__saveInstanceProperties(){this.constructor.elementProperties.forEach((e,t)=>{this.hasOwnProperty(t)&&(this.__instanceProperties.set(t,this[t]),delete this[t])})}createRenderRoot(){var e;const t=(e=this.shadowRoot)!==null&&e!==void 0?e:this.attachShadow(this.constructor.shadowRootOptions);return on(t,this.constructor.elementStyles),t}connectedCallback(){var e;this.renderRoot===void 0&&(this.renderRoot=this.createRenderRoot()),this.enableUpdating(!0),(e=this.__controllers)===null||e===void 0||e.forEach(t=>{var i;return(i=t.hostConnected)===null||i===void 0?void 0:i.call(t)})}enableUpdating(e){}disconnectedCallback(){var e;(e=this.__controllers)===null||e===void 0||e.forEach(t=>{var i;return(i=t.hostDisconnected)===null||i===void 0?void 0:i.call(t)})}attributeChangedCallback(e,t,i){this._$attributeToProperty(e,i)}__propertyToAttribute(e,t,i=dt){var n;const r=this.constructor.__attributeNameForProperty(e,i);if(r!==void 0&&i.reflect===!0){const l=(((n=i.converter)===null||n===void 0?void 0:n.toAttribute)!==void 0?i.converter:kt).toAttribute(t,i.type);this.constructor.enabledWarnings.indexOf("migration")>=0&&l===void 0&&J("undefined-attribute-value",`The attribute value for the ${e} property is undefined on element ${this.localName}. The attribute will be removed, but in the previous version of \`ReactiveElement\`, the attribute would not have changed.`),this.__reflectingProperty=e,l==null?this.removeAttribute(r):this.setAttribute(r,l),this.__reflectingProperty=null}}_$attributeToProperty(e,t){var i;const n=this.constructor,r=n.__attributeToPropertyMap.get(e);if(r!==void 0&&this.__reflectingProperty!==r){const s=n.getPropertyOptions(r),l=typeof s.converter=="function"?{fromAttribute:s.converter}:((i=s.converter)===null||i===void 0?void 0:i.fromAttribute)!==void 0?s.converter:kt;this.__reflectingProperty=r,this[r]=l.fromAttribute(t,s.type),this.__reflectingProperty=null}}requestUpdate(e,t,i){let n=!0;return e!==void 0&&(i=i||this.constructor.getPropertyOptions(e),(i.hasChanged||Ho)(this[e],t)?(this._$changedProperties.has(e)||this._$changedProperties.set(e,t),i.reflect===!0&&this.__reflectingProperty!==e&&(this.__reflectingProperties===void 0&&(this.__reflectingProperties=new Map),this.__reflectingProperties.set(e,i))):n=!1),!this.isUpdatePending&&n&&(this.__updatePromise=this.__enqueueUpdate()),Fo(this.localName)}async __enqueueUpdate(){this.isUpdatePending=!0;try{await this.__updatePromise}catch(t){Promise.reject(t)}const e=this.scheduleUpdate();return e!=null&&await e,!this.isUpdatePending}scheduleUpdate(){return this.performUpdate()}performUpdate(){var e,t;if(!this.isUpdatePending)return;if(ct==null||ct({kind:"update"}),!this.hasUpdated){const r=[];if((e=this.constructor.__reactivePropertyKeys)===null||e===void 0||e.forEach(s=>{var l;this.hasOwnProperty(s)&&!(!((l=this.__instanceProperties)===null||l===void 0)&&l.has(s))&&r.push(s)}),r.length)throw new Error(`The following properties on element ${this.localName} will not trigger updates as expected because they are set using class fields: ${r.join(", ")}. Native class fields and some compiled output will overwrite accessors used for detecting changes. See https://lit.dev/msg/class-field-shadowing for more information.`)}this.__instanceProperties&&(this.__instanceProperties.forEach((r,s)=>this[s]=r),this.__instanceProperties=void 0);let i=!1;const n=this._$changedProperties;try{i=this.shouldUpdate(n),i?(this.willUpdate(n),(t=this.__controllers)===null||t===void 0||t.forEach(r=>{var s;return(s=r.hostUpdate)===null||s===void 0?void 0:s.call(r)}),this.update(n)):this.__markUpdated()}catch(r){throw i=!1,this.__markUpdated(),r}i&&this._$didUpdate(n)}willUpdate(e){}_$didUpdate(e){var t;(t=this.__controllers)===null||t===void 0||t.forEach(i=>{var n;return(n=i.hostUpdated)===null||n===void 0?void 0:n.call(i)}),this.hasUpdated||(this.hasUpdated=!0,this.firstUpdated(e)),this.updated(e),this.isUpdatePending&&this.constructor.enabledWarnings.indexOf("change-in-update")>=0&&J("change-in-update",`Element ${this.localName} scheduled an update (generally because a property was set) after an update completed, causing a new update to be scheduled. This is inefficient and should be avoided unless the next update can only be scheduled as a side effect of the previous update.`)}__markUpdated(){this._$changedProperties=new Map,this.isUpdatePending=!1}get updateComplete(){return this.getUpdateComplete()}getUpdateComplete(){return this.__updatePromise}shouldUpdate(e){return!0}update(e){this.__reflectingProperties!==void 0&&(this.__reflectingProperties.forEach((t,i)=>this.__propertyToAttribute(i,this[i],t)),this.__reflectingProperties=void 0),this.__markUpdated()}updated(e){}firstUpdated(e){}}Uo=Rt;B[Uo]=!0;B.elementProperties=new Map;B.elementStyles=[];B.shadowRootOptions={mode:"open"};Ue==null||Ue({ReactiveElement:B});{B.enabledWarnings=["change-in-update"];const o=function(e){e.hasOwnProperty(Bo("enabledWarnings"))||(e.enabledWarnings=e.enabledWarnings.slice())};B.enableWarning=function(e){o(this),this.enabledWarnings.indexOf(e)<0&&this.enabledWarnings.push(e)},B.disableWarning=function(e){o(this);const t=this.enabledWarnings.indexOf(e);t>=0&&this.enabledWarnings.splice(t,1)}}((lt=F.reactiveElementVersions)!==null&&lt!==void 0?lt:F.reactiveElementVersions=[]).push("1.6.1");F.reactiveElementVersions.length>1&&J("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var ut,mt,ht,pt;const L=window,g=o=>{L.emitLitDebugLogEvents&&L.dispatchEvent(new CustomEvent("lit-debug",{detail:o}))};let sn=0,Ke;(ut=L.litIssuedWarnings)!==null&&ut!==void 0||(L.litIssuedWarnings=new Set),Ke=(o,e)=>{e+=o?` See https://lit.dev/msg/${o} for more information.`:"",L.litIssuedWarnings.has(e)||(console.warn(e),L.litIssuedWarnings.add(e))},Ke("dev-mode","Lit is in dev mode. Not recommended for production!");const P=!((mt=L.ShadyDOM)===null||mt===void 0)&&mt.inUse&&((ht=L.ShadyDOM)===null||ht===void 0?void 0:ht.noPatch)===!0?L.ShadyDOM.wrap:o=>o,de=L.trustedTypes,fo=de?de.createPolicy("lit-html",{createHTML:o=>o}):void 0,an=o=>o,Xe=(o,e,t)=>an,ln=o=>{if(ee!==Xe)throw new Error("Attempted to overwrite existing lit-html security policy. setSanitizeDOMValueFactory should be called at most once.");ee=o},cn=()=>{ee=Xe},Nt=(o,e,t)=>ee(o,e,t),Lt="$lit$",W=`lit$${String(Math.random()).slice(9)}$`,jo="?"+W,dn=`<${jo}>`,ue=document,_e=()=>ue.createComment(""),Se=o=>o===null||typeof o!="object"&&typeof o!="function",Wo=Array.isArray,un=o=>Wo(o)||typeof(o==null?void 0:o[Symbol.iterator])=="function",ft=`[ 	
\f\r]`,mn=`[^ 	
\f\r"'\`<>=]`,hn=`[^\\s"'>=/]`,ge=/<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g,go=1,gt=2,pn=3,vo=/-->/g,yo=/>/g,X=new RegExp(`>|${ft}(?:(${hn}+)(${ft}*=${ft}*(?:${mn}|("|')|))|$)`,"g"),fn=0,bo=1,gn=2,xo=3,vt=/'/g,yt=/"/g,Go=/^(?:script|style|textarea|title)$/i,vn=1,Ye=2,Mt=1,Je=2,yn=3,bn=4,xn=5,Dt=6,wn=7,qo=o=>(e,...t)=>(e.some(i=>i===void 0)&&console.warn(`Some template strings are undefined.
This is probably caused by illegal octal escape sequences.`),{_$litType$:o,strings:e,values:t}),R=qo(vn),_r=qo(Ye),Z=Symbol.for("lit-noChange"),S=Symbol.for("lit-nothing"),wo=new WeakMap,ce=ue.createTreeWalker(ue,129,null,!1);let ee=Xe;const _n=(o,e)=>{const t=o.length-1,i=[];let n=e===Ye?"<svg>":"",r,s=ge;for(let a=0;a<t;a++){const c=o[a];let u=-1,p,h=0,f;for(;h<c.length&&(s.lastIndex=h,f=s.exec(c),f!==null);)if(h=s.lastIndex,s===ge){if(f[go]==="!--")s=vo;else if(f[go]!==void 0)s=yo;else if(f[gt]!==void 0)Go.test(f[gt])&&(r=new RegExp(`</${f[gt]}`,"g")),s=X;else if(f[pn]!==void 0)throw new Error("Bindings in tag names are not supported. Please use static templates instead. See https://lit.dev/docs/templates/expressions/#static-expressions")}else s===X?f[fn]===">"?(s=r??ge,u=-1):f[bo]===void 0?u=-2:(u=s.lastIndex-f[gn].length,p=f[bo],s=f[xo]===void 0?X:f[xo]==='"'?yt:vt):s===yt||s===vt?s=X:s===vo||s===yo?s=ge:(s=X,r=void 0);console.assert(u===-1||s===X||s===vt||s===yt,"unexpected parse state B");const G=s===X&&o[a+1].startsWith("/>")?" ":"";n+=s===ge?c+dn:u>=0?(i.push(p),c.slice(0,u)+Lt+c.slice(u)+W+G):c+W+(u===-2?(i.push(void 0),a):G)}const l=n+(o[t]||"<?>")+(e===Ye?"</svg>":"");if(!Array.isArray(o)||!o.hasOwnProperty("raw")){let a="invalid template strings array";throw a=`
          Internal Error: expected template strings to be an array
          with a 'raw' field. Faking a template strings array by
          calling html or svg like an ordinary function is effectively
          the same as calling unsafeHtml and can lead to major security
          issues, e.g. opening your code up to XSS attacks.

          If you're using the html or svg tagged template functions normally
          and still seeing this error, please file a bug at
          https://github.com/lit/lit/issues/new?template=bug_report.md
          and include information about your build tooling, if any.
        `.trim().replace(/\n */g,`
`),new Error(a)}return[fo!==void 0?fo.createHTML(l):l,i]};class Ee{constructor({strings:e,["_$litType$"]:t},i){this.parts=[];let n,r=0,s=0;const l=e.length-1,a=this.parts,[c,u]=_n(e,t);if(this.el=Ee.createElement(c,i),ce.currentNode=this.el.content,t===Ye){const p=this.el.content,h=p.firstChild;h.remove(),p.append(...h.childNodes)}for(;(n=ce.nextNode())!==null&&a.length<l;){if(n.nodeType===1){{const p=n.localName;if(/^(?:textarea|template)$/i.test(p)&&n.innerHTML.includes(W)){const h=`Expressions are not supported inside \`${p}\` elements. See https://lit.dev/msg/expression-in-${p} for more information.`;if(p==="template")throw new Error(h);Ke("",h)}}if(n.hasAttributes()){const p=[];for(const h of n.getAttributeNames())if(h.endsWith(Lt)||h.startsWith(W)){const f=u[s++];if(p.push(h),f!==void 0){const q=n.getAttribute(f.toLowerCase()+Lt).split(W),H=/([.?@])?(.*)/.exec(f);a.push({type:Mt,index:r,name:H[2],strings:q,ctor:H[1]==="."?En:H[1]==="?"?Tn:H[1]==="@"?$n:Qe})}else a.push({type:Dt,index:r})}for(const h of p)n.removeAttribute(h)}if(Go.test(n.tagName)){const p=n.textContent.split(W),h=p.length-1;if(h>0){n.textContent=de?de.emptyScript:"";for(let f=0;f<h;f++)n.append(p[f],_e()),ce.nextNode(),a.push({type:Je,index:++r});n.append(p[h],_e())}}}else if(n.nodeType===8)if(n.data===jo)a.push({type:Je,index:r});else{let h=-1;for(;(h=n.data.indexOf(W,h+1))!==-1;)a.push({type:wn,index:r}),h+=W.length-1}r++}g==null||g({kind:"template prep",template:this,clonableTemplate:this.el,parts:this.parts,strings:e})}static createElement(e,t){const i=ue.createElement("template");return i.innerHTML=e,i}}function me(o,e,t=o,i){var n,r,s,l;if(e===Z)return e;let a=i!==void 0?(n=t.__directives)===null||n===void 0?void 0:n[i]:t.__directive;const c=Se(e)?void 0:e._$litDirective$;return(a==null?void 0:a.constructor)!==c&&((r=a==null?void 0:a._$notifyDirectiveConnectionChanged)===null||r===void 0||r.call(a,!1),c===void 0?a=void 0:(a=new c(o),a._$initialize(o,t,i)),i!==void 0?((s=(l=t).__directives)!==null&&s!==void 0?s:l.__directives=[])[i]=a:t.__directive=a),a!==void 0&&(e=me(o,a._$resolve(o,e.values),a,i)),e}class Sn{constructor(e,t){this._$parts=[],this._$disconnectableChildren=void 0,this._$template=e,this._$parent=t}get parentNode(){return this._$parent.parentNode}get _$isConnected(){return this._$parent._$isConnected}_clone(e){var t;const{el:{content:i},parts:n}=this._$template,r=((t=e==null?void 0:e.creationScope)!==null&&t!==void 0?t:ue).importNode(i,!0);ce.currentNode=r;let s=ce.nextNode(),l=0,a=0,c=n[0];for(;c!==void 0;){if(l===c.index){let u;c.type===Je?u=new $e(s,s.nextSibling,this,e):c.type===Mt?u=new c.ctor(s,c.name,c.strings,this,e):c.type===Dt&&(u=new kn(s,this,e)),this._$parts.push(u),c=n[++a]}l!==(c==null?void 0:c.index)&&(s=ce.nextNode(),l++)}return r}_update(e){let t=0;for(const i of this._$parts)i!==void 0&&(g==null||g({kind:"set part",part:i,value:e[t],valueIndex:t,values:e,templateInstance:this}),i.strings!==void 0?(i._$setValue(e,i,t),t+=i.strings.length-2):i._$setValue(e[t])),t++}}class $e{constructor(e,t,i,n){var r;this.type=Je,this._$committedValue=S,this._$disconnectableChildren=void 0,this._$startNode=e,this._$endNode=t,this._$parent=i,this.options=n,this.__isConnected=(r=n==null?void 0:n.isConnected)!==null&&r!==void 0?r:!0,this._textSanitizer=void 0}get _$isConnected(){var e,t;return(t=(e=this._$parent)===null||e===void 0?void 0:e._$isConnected)!==null&&t!==void 0?t:this.__isConnected}get parentNode(){let e=P(this._$startNode).parentNode;const t=this._$parent;return t!==void 0&&(e==null?void 0:e.nodeType)===11&&(e=t.parentNode),e}get startNode(){return this._$startNode}get endNode(){return this._$endNode}_$setValue(e,t=this){var i;if(this.parentNode===null)throw new Error("This `ChildPart` has no `parentNode` and therefore cannot accept a value. This likely means the element containing the part was manipulated in an unsupported way outside of Lit's control such that the part's marker nodes were ejected from DOM. For example, setting the element's `innerHTML` or `textContent` can do this.");if(e=me(this,e,t),Se(e))e===S||e==null||e===""?(this._$committedValue!==S&&(g==null||g({kind:"commit nothing to child",start:this._$startNode,end:this._$endNode,parent:this._$parent,options:this.options}),this._$clear()),this._$committedValue=S):e!==this._$committedValue&&e!==Z&&this._commitText(e);else if(e._$litType$!==void 0)this._commitTemplateResult(e);else if(e.nodeType!==void 0){if(((i=this.options)===null||i===void 0?void 0:i.host)===e){this._commitText("[probable mistake: rendered a template's host in itself (commonly caused by writing ${this} in a template]"),console.warn("Attempted to render the template host",e,"inside itself. This is almost always a mistake, and in dev mode ","we render some warning text. In production however, we'll ","render it, which will usually result in an error, and sometimes ","in the element disappearing from the DOM.");return}this._commitNode(e)}else un(e)?this._commitIterable(e):this._commitText(e)}_insert(e){return P(P(this._$startNode).parentNode).insertBefore(e,this._$endNode)}_commitNode(e){var t;if(this._$committedValue!==e){if(this._$clear(),ee!==Xe){const i=(t=this._$startNode.parentNode)===null||t===void 0?void 0:t.nodeName;if(i==="STYLE"||i==="SCRIPT"){let n="Forbidden";throw i==="STYLE"?n="Lit does not support binding inside style nodes. This is a security risk, as style injection attacks can exfiltrate data and spoof UIs. Consider instead using css`...` literals to compose styles, and make do dynamic styling with css custom properties, ::parts, <slot>s, and by mutating the DOM rather than stylesheets.":n="Lit does not support binding inside script nodes. This is a security risk, as it could allow arbitrary code execution.",new Error(n)}}g==null||g({kind:"commit node",start:this._$startNode,parent:this._$parent,value:e,options:this.options}),this._$committedValue=this._insert(e)}}_commitText(e){if(this._$committedValue!==S&&Se(this._$committedValue)){const t=P(this._$startNode).nextSibling;this._textSanitizer===void 0&&(this._textSanitizer=Nt(t,"data","property")),e=this._textSanitizer(e),g==null||g({kind:"commit text",node:t,value:e,options:this.options}),t.data=e}else{const t=ue.createTextNode("");this._commitNode(t),this._textSanitizer===void 0&&(this._textSanitizer=Nt(t,"data","property")),e=this._textSanitizer(e),g==null||g({kind:"commit text",node:t,value:e,options:this.options}),t.data=e}this._$committedValue=e}_commitTemplateResult(e){var t;const{values:i,["_$litType$"]:n}=e,r=typeof n=="number"?this._$getTemplate(e):(n.el===void 0&&(n.el=Ee.createElement(n.h,this.options)),n);if(((t=this._$committedValue)===null||t===void 0?void 0:t._$template)===r)g==null||g({kind:"template updating",template:r,instance:this._$committedValue,parts:this._$committedValue._$parts,options:this.options,values:i}),this._$committedValue._update(i);else{const s=new Sn(r,this),l=s._clone(this.options);g==null||g({kind:"template instantiated",template:r,instance:s,parts:s._$parts,options:this.options,fragment:l,values:i}),s._update(i),g==null||g({kind:"template instantiated and updated",template:r,instance:s,parts:s._$parts,options:this.options,fragment:l,values:i}),this._commitNode(l),this._$committedValue=s}}_$getTemplate(e){let t=wo.get(e.strings);return t===void 0&&wo.set(e.strings,t=new Ee(e)),t}_commitIterable(e){Wo(this._$committedValue)||(this._$committedValue=[],this._$clear());const t=this._$committedValue;let i=0,n;for(const r of e)i===t.length?t.push(n=new $e(this._insert(_e()),this._insert(_e()),this,this.options)):n=t[i],n._$setValue(r),i++;i<t.length&&(this._$clear(n&&P(n._$endNode).nextSibling,i),t.length=i)}_$clear(e=P(this._$startNode).nextSibling,t){var i;for((i=this._$notifyConnectionChanged)===null||i===void 0||i.call(this,!1,!0,t);e&&e!==this._$endNode;){const n=P(e).nextSibling;P(e).remove(),e=n}}setConnected(e){var t;if(this._$parent===void 0)this.__isConnected=e,(t=this._$notifyConnectionChanged)===null||t===void 0||t.call(this,e);else throw new Error("part.setConnected() may only be called on a RootPart returned from render().")}}class Qe{constructor(e,t,i,n,r){this.type=Mt,this._$committedValue=S,this._$disconnectableChildren=void 0,this.element=e,this.name=t,this._$parent=n,this.options=r,i.length>2||i[0]!==""||i[1]!==""?(this._$committedValue=new Array(i.length-1).fill(new String),this.strings=i):this._$committedValue=S,this._sanitizer=void 0}get tagName(){return this.element.tagName}get _$isConnected(){return this._$parent._$isConnected}_$setValue(e,t=this,i,n){const r=this.strings;let s=!1;if(r===void 0)e=me(this,e,t,0),s=!Se(e)||e!==this._$committedValue&&e!==Z,s&&(this._$committedValue=e);else{const l=e;e=r[0];let a,c;for(a=0;a<r.length-1;a++)c=me(this,l[i+a],t,a),c===Z&&(c=this._$committedValue[a]),s||(s=!Se(c)||c!==this._$committedValue[a]),c===S?e=S:e!==S&&(e+=(c??"")+r[a+1]),this._$committedValue[a]=c}s&&!n&&this._commitValue(e)}_commitValue(e){e===S?P(this.element).removeAttribute(this.name):(this._sanitizer===void 0&&(this._sanitizer=ee(this.element,this.name,"attribute")),e=this._sanitizer(e??""),g==null||g({kind:"commit attribute",element:this.element,name:this.name,value:e,options:this.options}),P(this.element).setAttribute(this.name,e??""))}}class En extends Qe{constructor(){super(...arguments),this.type=yn}_commitValue(e){this._sanitizer===void 0&&(this._sanitizer=ee(this.element,this.name,"property")),e=this._sanitizer(e),g==null||g({kind:"commit property",element:this.element,name:this.name,value:e,options:this.options}),this.element[this.name]=e===S?void 0:e}}const Cn=de?de.emptyScript:"";class Tn extends Qe{constructor(){super(...arguments),this.type=bn}_commitValue(e){g==null||g({kind:"commit boolean attribute",element:this.element,name:this.name,value:!!(e&&e!==S),options:this.options}),e&&e!==S?P(this.element).setAttribute(this.name,Cn):P(this.element).removeAttribute(this.name)}}class $n extends Qe{constructor(e,t,i,n,r){if(super(e,t,i,n,r),this.type=xn,this.strings!==void 0)throw new Error(`A \`<${e.localName}>\` has a \`@${t}=...\` listener with invalid content. Event listeners in templates must have exactly one expression and no surrounding text.`)}_$setValue(e,t=this){var i;if(e=(i=me(this,e,t,0))!==null&&i!==void 0?i:S,e===Z)return;const n=this._$committedValue,r=e===S&&n!==S||e.capture!==n.capture||e.once!==n.once||e.passive!==n.passive,s=e!==S&&(n===S||r);g==null||g({kind:"commit event listener",element:this.element,name:this.name,value:e,options:this.options,removeListener:r,addListener:s,oldListener:n}),r&&this.element.removeEventListener(this.name,this,n),s&&this.element.addEventListener(this.name,this,e),this._$committedValue=e}handleEvent(e){var t,i;typeof this._$committedValue=="function"?this._$committedValue.call((i=(t=this.options)===null||t===void 0?void 0:t.host)!==null&&i!==void 0?i:this.element,e):this._$committedValue.handleEvent(e)}}class kn{constructor(e,t,i){this.element=e,this.type=Dt,this._$disconnectableChildren=void 0,this._$parent=t,this.options=i}get _$isConnected(){return this._$parent._$isConnected}_$setValue(e){g==null||g({kind:"commit to element binding",element:this.element,value:e,options:this.options}),me(this,e)}}const bt=L.litHtmlPolyfillSupportDevMode;bt==null||bt(Ee,$e);((pt=L.litHtmlVersions)!==null&&pt!==void 0?pt:L.litHtmlVersions=[]).push("2.7.2");L.litHtmlVersions.length>1&&Ke("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");const Fe=(o,e,t)=>{var i,n;if(e==null)throw new TypeError(`The container to render into may not be ${e}`);const r=sn++,s=(i=t==null?void 0:t.renderBefore)!==null&&i!==void 0?i:e;let l=s._$litPart$;if(g==null||g({kind:"begin render",id:r,value:o,container:e,options:t,part:l}),l===void 0){const a=(n=t==null?void 0:t.renderBefore)!==null&&n!==void 0?n:null;s._$litPart$=l=new $e(e.insertBefore(_e(),a),a,void 0,t??{})}return l._$setValue(o),g==null||g({kind:"end render",id:r,value:o,container:e,options:t,part:l}),l};Fe.setSanitizer=ln,Fe.createSanitizer=Nt,Fe._testOnlyClearSanitizerFactoryDoNotCallOrElse=cn;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var xt,wt,_t;let Vt;{const o=(xt=globalThis.litIssuedWarnings)!==null&&xt!==void 0?xt:globalThis.litIssuedWarnings=new Set;Vt=(e,t)=>{t+=` See https://lit.dev/msg/${e} for more information.`,o.has(t)||(console.warn(t),o.add(t))}}class te extends B{constructor(){super(...arguments),this.renderOptions={host:this},this.__childPart=void 0}createRenderRoot(){var e,t;const i=super.createRenderRoot();return(e=(t=this.renderOptions).renderBefore)!==null&&e!==void 0||(t.renderBefore=i.firstChild),i}update(e){const t=this.render();this.hasUpdated||(this.renderOptions.isConnected=this.isConnected),super.update(e),this.__childPart=Fe(t,this.renderRoot,this.renderOptions)}connectedCallback(){var e;super.connectedCallback(),(e=this.__childPart)===null||e===void 0||e.setConnected(!0)}disconnectedCallback(){var e;super.disconnectedCallback(),(e=this.__childPart)===null||e===void 0||e.setConnected(!1)}render(){return Z}}te.finalized=!0;te._$litElement$=!0;(wt=globalThis.litElementHydrateSupport)===null||wt===void 0||wt.call(globalThis,{LitElement:te});const St=globalThis.litElementPolyfillSupportDevMode;St==null||St({LitElement:te});te.finalize=function(){if(!B.finalize.call(this))return!1;const e=(t,i,n=!1)=>{if(t.hasOwnProperty(i)){const r=(typeof t=="function"?t:t.constructor).name;Vt(n?"renamed-api":"removed-api",`\`${i}\` is implemented on class ${r}. It has been ${n?"renamed":"removed"} in this version of LitElement.`)}};return e(this,"render"),e(this,"getStyles",!0),e(this.prototype,"adoptStyles"),!0};((_t=globalThis.litElementVersions)!==null&&_t!==void 0?_t:globalThis.litElementVersions=[]).push("3.3.1");globalThis.litElementVersions.length>1&&Vt("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Rn=(o,e)=>e.kind==="method"&&e.descriptor&&!("value"in e.descriptor)?{...e,finisher(t){t.createProperty(e.key,o)}}:{kind:"field",key:Symbol(),placement:"own",descriptor:{},originalKey:e.key,initializer(){typeof e.initializer=="function"&&(this[e.key]=e.initializer.call(this))},finisher(t){t.createProperty(e.key,o)}},Nn=(o,e,t)=>{e.constructor.createProperty(t,o)};function _(o){return(e,t)=>t!==void 0?Nn(o,e,t):Rn(o,e)}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */function fe(o){return _({...o,state:!0})}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Ln=({finisher:o,descriptor:e})=>(t,i)=>{var n;if(i!==void 0){const r=t.constructor;e!==void 0&&Object.defineProperty(t,i,e(i)),o==null||o(r,i)}else{const r=(n=t.originalKey)!==null&&n!==void 0?n:t.key,s=e!=null?{kind:"method",placement:"prototype",key:r,descriptor:e(t.key)}:{...t,key:r};return o!=null&&(s.finisher=function(l){o(l,r)}),s}};/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */function In(o,e){return Ln({descriptor:t=>{const i={get(){var n,r;return(r=(n=this.renderRoot)===null||n===void 0?void 0:n.querySelector(o))!==null&&r!==void 0?r:null},enumerable:!0,configurable:!0};if(e){const n=typeof t=="symbol"?Symbol():`__${t}`;i.get=function(){var r,s;return this[n]===void 0&&(this[n]=(s=(r=this.renderRoot)===null||r===void 0?void 0:r.querySelector(o))!==null&&s!==void 0?s:null),this[n]}}return i}})}/**
 * @license
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var Et;const An=window;((Et=An.HTMLSlotElement)===null||Et===void 0?void 0:Et.prototype.assignedElements)!=null;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Pn={ATTRIBUTE:1,CHILD:2,PROPERTY:3,BOOLEAN_ATTRIBUTE:4,EVENT:5,ELEMENT:6},On=o=>(...e)=>({_$litDirective$:o,values:e});class zn{constructor(e){}get _$isConnected(){return this._$parent._$isConnected}_$initialize(e,t,i){this.__part=e,this._$parent=t,this.__attributeIndex=i}_$resolve(e,t){return this.update(e,t)}update(e,t){return this.render(...t)}}/**
 * @license
 * Copyright 2018 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */class Mn extends zn{constructor(e){var t;if(super(e),e.type!==Pn.ATTRIBUTE||e.name!=="class"||((t=e.strings)===null||t===void 0?void 0:t.length)>2)throw new Error("`classMap()` can only be used in the `class` attribute and must be the only part in the attribute.")}render(e){return" "+Object.keys(e).filter(t=>e[t]).join(" ")+" "}update(e,[t]){var i,n;if(this._previousClasses===void 0){this._previousClasses=new Set,e.strings!==void 0&&(this._staticClasses=new Set(e.strings.join(" ").split(/\s/).filter(s=>s!=="")));for(const s in t)t[s]&&!(!((i=this._staticClasses)===null||i===void 0)&&i.has(s))&&this._previousClasses.add(s);return this.render(t)}const r=e.element.classList;this._previousClasses.forEach(s=>{s in t||(r.remove(s),this._previousClasses.delete(s))});for(const s in t){const l=!!t[s];l!==this._previousClasses.has(s)&&!(!((n=this._staticClasses)===null||n===void 0)&&n.has(s))&&(l?(r.add(s),this._previousClasses.add(s)):(r.remove(s),this._previousClasses.delete(s)))}return Z}}const Ko=On(Mn),Ct="css-loading-indicator";var z;(function(o){o.IDLE="",o.FIRST="first",o.SECOND="second",o.THIRD="third"})(z||(z={}));class C extends te{constructor(){super(),this.firstDelay=450,this.secondDelay=1500,this.thirdDelay=5e3,this.expandedDuration=2e3,this.onlineText="Online",this.offlineText="Connection lost",this.reconnectingText="Connection lost, trying to reconnect...",this.offline=!1,this.reconnecting=!1,this.expanded=!1,this.loading=!1,this.loadingBarState=z.IDLE,this.applyDefaultThemeState=!0,this.firstTimeout=0,this.secondTimeout=0,this.thirdTimeout=0,this.expandedTimeout=0,this.lastMessageState=E.CONNECTED,this.connectionStateListener=()=>{this.expanded=this.updateConnectionState(),this.expandedTimeout=this.timeoutFor(this.expandedTimeout,this.expanded,()=>{this.expanded=!1},this.expandedDuration)}}static create(){var e,t;const i=window;return!((e=i.Vaadin)===null||e===void 0)&&e.connectionIndicator||(i.Vaadin=i.Vaadin||{},i.Vaadin.connectionIndicator=document.createElement("vaadin-connection-indicator"),document.body.appendChild(i.Vaadin.connectionIndicator)),(t=i.Vaadin)===null||t===void 0?void 0:t.connectionIndicator}render(){return R`
      <div class="v-loading-indicator ${this.loadingBarState}" style=${this.getLoadingBarStyle()}></div>

      <div
        class="v-status-message ${Ko({active:this.reconnecting})}"
      >
        <span class="text"> ${this.renderMessage()} </span>
      </div>
    `}connectedCallback(){var e;super.connectedCallback();const t=window;!((e=t.Vaadin)===null||e===void 0)&&e.connectionState&&(this.connectionStateStore=t.Vaadin.connectionState,this.connectionStateStore.addStateChangeListener(this.connectionStateListener),this.updateConnectionState()),this.updateTheme()}disconnectedCallback(){super.disconnectedCallback(),this.connectionStateStore&&this.connectionStateStore.removeStateChangeListener(this.connectionStateListener),this.updateTheme()}get applyDefaultTheme(){return this.applyDefaultThemeState}set applyDefaultTheme(e){e!==this.applyDefaultThemeState&&(this.applyDefaultThemeState=e,this.updateTheme())}createRenderRoot(){return this}updateConnectionState(){var e;const t=(e=this.connectionStateStore)===null||e===void 0?void 0:e.state;return this.offline=t===E.CONNECTION_LOST,this.reconnecting=t===E.RECONNECTING,this.updateLoading(t===E.LOADING),this.loading?!1:t!==this.lastMessageState?(this.lastMessageState=t,!0):!1}updateLoading(e){this.loading=e,this.loadingBarState=z.IDLE,this.firstTimeout=this.timeoutFor(this.firstTimeout,e,()=>{this.loadingBarState=z.FIRST},this.firstDelay),this.secondTimeout=this.timeoutFor(this.secondTimeout,e,()=>{this.loadingBarState=z.SECOND},this.secondDelay),this.thirdTimeout=this.timeoutFor(this.thirdTimeout,e,()=>{this.loadingBarState=z.THIRD},this.thirdDelay)}renderMessage(){return this.reconnecting?this.reconnectingText:this.offline?this.offlineText:this.onlineText}updateTheme(){if(this.applyDefaultThemeState&&this.isConnected){if(!document.getElementById(Ct)){const e=document.createElement("style");e.id=Ct,e.textContent=this.getDefaultStyle(),document.head.appendChild(e)}}else{const e=document.getElementById(Ct);e&&document.head.removeChild(e)}}getDefaultStyle(){return`
      @keyframes v-progress-start {
        0% {
          width: 0%;
        }
        100% {
          width: 50%;
        }
      }
      @keyframes v-progress-delay {
        0% {
          width: 50%;
        }
        100% {
          width: 90%;
        }
      }
      @keyframes v-progress-wait {
        0% {
          width: 90%;
          height: 4px;
        }
        3% {
          width: 91%;
          height: 7px;
        }
        100% {
          width: 96%;
          height: 7px;
        }
      }
      @keyframes v-progress-wait-pulse {
        0% {
          opacity: 1;
        }
        50% {
          opacity: 0.1;
        }
        100% {
          opacity: 1;
        }
      }
      .v-loading-indicator,
      .v-status-message {
        position: fixed;
        z-index: 251;
        left: 0;
        right: auto;
        top: 0;
        background-color: var(--lumo-primary-color, var(--material-primary-color, blue));
        transition: none;
      }
      .v-loading-indicator {
        width: 50%;
        height: 4px;
        opacity: 1;
        pointer-events: none;
        animation: v-progress-start 1000ms 200ms both;
      }
      .v-loading-indicator[style*='none'] {
        display: block !important;
        width: 100%;
        opacity: 0;
        animation: none;
        transition: opacity 500ms 300ms, width 300ms;
      }
      .v-loading-indicator.second {
        width: 90%;
        animation: v-progress-delay 3.8s forwards;
      }
      .v-loading-indicator.third {
        width: 96%;
        animation: v-progress-wait 5s forwards, v-progress-wait-pulse 1s 4s infinite backwards;
      }

      vaadin-connection-indicator[offline] .v-loading-indicator,
      vaadin-connection-indicator[reconnecting] .v-loading-indicator {
        display: none;
      }

      .v-status-message {
        opacity: 0;
        width: 100%;
        max-height: var(--status-height-collapsed, 8px);
        overflow: hidden;
        background-color: var(--status-bg-color-online, var(--lumo-primary-color, var(--material-primary-color, blue)));
        color: var(
          --status-text-color-online,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        font-size: 0.75rem;
        font-weight: 600;
        line-height: 1;
        transition: all 0.5s;
        padding: 0 0.5em;
      }

      vaadin-connection-indicator[offline] .v-status-message,
      vaadin-connection-indicator[reconnecting] .v-status-message {
        opacity: 1;
        background-color: var(--status-bg-color-offline, var(--lumo-shade, #333));
        color: var(
          --status-text-color-offline,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        background-image: repeating-linear-gradient(
          45deg,
          rgba(255, 255, 255, 0),
          rgba(255, 255, 255, 0) 10px,
          rgba(255, 255, 255, 0.1) 10px,
          rgba(255, 255, 255, 0.1) 20px
        );
      }

      vaadin-connection-indicator[reconnecting] .v-status-message {
        animation: show-reconnecting-status 2s;
      }

      vaadin-connection-indicator[offline] .v-status-message:hover,
      vaadin-connection-indicator[reconnecting] .v-status-message:hover,
      vaadin-connection-indicator[expanded] .v-status-message {
        max-height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[expanded] .v-status-message {
        opacity: 1;
      }

      .v-status-message span {
        display: flex;
        align-items: center;
        justify-content: center;
        height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[reconnecting] .v-status-message span::before {
        content: '';
        width: 1em;
        height: 1em;
        border-top: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-left: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-right: 2px solid transparent;
        border-bottom: 2px solid transparent;
        border-radius: 50%;
        box-sizing: border-box;
        animation: v-spin 0.4s linear infinite;
        margin: 0 0.5em;
      }

      @keyframes v-spin {
        100% {
          transform: rotate(360deg);
        }
      }
    `}getLoadingBarStyle(){switch(this.loadingBarState){case z.IDLE:return"display: none";case z.FIRST:case z.SECOND:case z.THIRD:return"display: block";default:return""}}timeoutFor(e,t,i,n){return e!==0&&window.clearTimeout(e),t?window.setTimeout(i,n):0}static get instance(){return C.create()}}I([_({type:Number})],C.prototype,"firstDelay",void 0);I([_({type:Number})],C.prototype,"secondDelay",void 0);I([_({type:Number})],C.prototype,"thirdDelay",void 0);I([_({type:Number})],C.prototype,"expandedDuration",void 0);I([_({type:String})],C.prototype,"onlineText",void 0);I([_({type:String})],C.prototype,"offlineText",void 0);I([_({type:String})],C.prototype,"reconnectingText",void 0);I([_({type:Boolean,reflect:!0})],C.prototype,"offline",void 0);I([_({type:Boolean,reflect:!0})],C.prototype,"reconnecting",void 0);I([_({type:Boolean,reflect:!0})],C.prototype,"expanded",void 0);I([_({type:Boolean,reflect:!0})],C.prototype,"loading",void 0);I([_({type:String})],C.prototype,"loadingBarState",void 0);I([_({type:Boolean})],C.prototype,"applyDefaultTheme",null);customElements.get("vaadin-connection-indicator")===void 0&&customElements.define("vaadin-connection-indicator",C);C.instance;const Ce=window;Ce.Vaadin=Ce.Vaadin||{};Ce.Vaadin.registrations=Ce.Vaadin.registrations||[];Ce.Vaadin.registrations.push({is:"@vaadin/common-frontend",version:"0.0.18"});class _o extends Error{}const ve=window.document.body,w=window;class Dn{constructor(e){this.response=void 0,this.pathname="",this.isActive=!1,this.baseRegex=/^\//,ve.$=ve.$||[],this.config=e||{},w.Vaadin=w.Vaadin||{},w.Vaadin.Flow=w.Vaadin.Flow||{},w.Vaadin.Flow.clients={TypeScript:{isActive:()=>this.isActive}};const t=document.head.querySelector("base");this.baseRegex=new RegExp(`^${(document.baseURI||t&&t.href||"/").replace(/^https?:\/\/[^/]+/i,"")}`),this.appShellTitle=document.title,this.addConnectionIndicator()}get serverSideRoutes(){return[{path:"(.*)",action:this.action}]}loadingStarted(){this.isActive=!0,w.Vaadin.connectionState.loadingStarted()}loadingFinished(){this.isActive=!1,w.Vaadin.connectionState.loadingFinished()}get action(){return async e=>{if(this.pathname=e.pathname,w.Vaadin.connectionState.online)try{await this.flowInit()}catch(t){if(t instanceof _o)return w.Vaadin.connectionState.state=E.CONNECTION_LOST,this.offlineStubAction();throw t}else return this.offlineStubAction();return this.container.onBeforeEnter=(t,i)=>this.flowNavigate(t,i),this.container.onBeforeLeave=(t,i)=>this.flowLeave(t,i),this.container}}async flowLeave(e,t){const{connectionState:i}=w.Vaadin;return this.pathname===e.pathname||!this.isFlowClientLoaded()||i.offline?Promise.resolve({}):new Promise(n=>{this.loadingStarted(),this.container.serverConnected=r=>{n(t&&r?t.prevent():{}),this.loadingFinished()},ve.$server.leaveNavigation(this.getFlowRoutePath(e),this.getFlowRouteQuery(e))})}async flowNavigate(e,t){return this.response?new Promise(i=>{this.loadingStarted(),this.container.serverConnected=(n,r)=>{t&&n?i(t.prevent()):t&&t.redirect&&r?i(t.redirect(r.pathname)):(this.container.style.display="",i(this.container)),this.loadingFinished()},ve.$server.connectClient(this.container.localName,this.container.id,this.getFlowRoutePath(e),this.getFlowRouteQuery(e),this.appShellTitle,history.state)}):Promise.resolve(this.container)}getFlowRoutePath(e){return decodeURIComponent(e.pathname).replace(this.baseRegex,"")}getFlowRouteQuery(e){return e.search&&e.search.substring(1)||""}async flowInit(e=!1){if(!this.isFlowClientLoaded()){this.loadingStarted(),this.response=await this.flowInitUi(e),this.response.appConfig.clientRouting=!e;const{pushScript:t,appConfig:i}=this.response;typeof t=="string"&&await this.loadScript(t);const{appId:n}=i;await(await He(()=>import("./FlowBootstrap-feff2646.js"),[],import.meta.url)).init(this.response),typeof this.config.imports=="function"&&(this.injectAppIdScript(n),await this.config.imports());const s=await He(()=>import("./FlowClient-e0ae8105.js"),[],import.meta.url);if(await this.flowInitClient(s),!e){const l=`flow-container-${n.toLowerCase()}`;this.container=document.createElement(l),ve.$[n]=this.container,this.container.id=n}this.loadingFinished()}return this.container&&!this.container.isConnected&&(this.container.style.display="none",document.body.appendChild(this.container)),this.response}async loadScript(e){return new Promise((t,i)=>{const n=document.createElement("script");n.onload=()=>t(),n.onerror=i,n.src=e,document.body.appendChild(n)})}injectAppIdScript(e){const t=e.substring(0,e.lastIndexOf("-")),i=document.createElement("script");i.type="module",i.setAttribute("data-app-id",t),document.body.append(i)}async flowInitClient(e){return e.init(),new Promise(t=>{const i=setInterval(()=>{Object.keys(w.Vaadin.Flow.clients).filter(r=>r!=="TypeScript").reduce((r,s)=>r||w.Vaadin.Flow.clients[s].isActive(),!1)||(clearInterval(i),t())},5)})}async flowInitUi(e){const t=w.Vaadin&&w.Vaadin.TypeScript&&w.Vaadin.TypeScript.initial;return t?(w.Vaadin.TypeScript.initial=void 0,Promise.resolve(t)):new Promise((i,n)=>{const s=new XMLHttpRequest,l=e?"&serverSideRouting":"",a=`?v-r=init&location=${encodeURIComponent(this.getFlowRoutePath(location))}&query=${encodeURIComponent(this.getFlowRouteQuery(location))}${l}`;s.open("GET",a),s.onerror=()=>n(new _o(`Invalid server response when initializing Flow UI.
        ${s.status}
        ${s.responseText}`)),s.onload=()=>{const c=s.getResponseHeader("content-type");c&&c.indexOf("application/json")!==-1?i(JSON.parse(s.responseText)):s.onerror()},s.send()})}addConnectionIndicator(){C.create(),w.addEventListener("online",()=>{if(!this.isFlowClientLoaded()){w.Vaadin.connectionState.state=E.RECONNECTING;const e=new XMLHttpRequest;e.open("HEAD","sw.js"),e.onload=()=>{w.Vaadin.connectionState.state=E.CONNECTED},e.onerror=()=>{w.Vaadin.connectionState.state=E.CONNECTION_LOST},setTimeout(()=>e.send(),50)}}),w.addEventListener("offline",()=>{this.isFlowClientLoaded()||(w.Vaadin.connectionState.state=E.CONNECTION_LOST)})}async offlineStubAction(){const e=document.createElement("iframe"),t="./offline-stub.html";e.setAttribute("src",t),e.setAttribute("style","width: 100%; height: 100%; border: 0"),this.response=void 0;let i;const n=()=>{i!==void 0&&(w.Vaadin.connectionState.removeStateChangeListener(i),i=void 0)};return e.onBeforeEnter=(r,s,l)=>{i=()=>{w.Vaadin.connectionState.online&&(n(),l.render(r,!1))},w.Vaadin.connectionState.addStateChangeListener(i)},e.onBeforeLeave=(r,s,l)=>{n()},e}isFlowClientLoaded(){return this.response!==void 0}}const{serverSideRoutes:Vn}=new Dn({imports:()=>He(()=>import("./generated-flow-imports-f0ae24da.js"),[],import.meta.url)}),Un=[...Vn],Fn=new Y(document.querySelector("#outlet"));Fn.setRoutes(Un);var Bn=function(){var o=document.getSelection();if(!o.rangeCount)return function(){};for(var e=document.activeElement,t=[],i=0;i<o.rangeCount;i++)t.push(o.getRangeAt(i));switch(e.tagName.toUpperCase()){case"INPUT":case"TEXTAREA":e.blur();break;default:e=null;break}return o.removeAllRanges(),function(){o.type==="Caret"&&o.removeAllRanges(),o.rangeCount||t.forEach(function(n){o.addRange(n)}),e&&e.focus()}},So={"text/plain":"Text","text/html":"Url",default:"Text"},Hn="Copy to clipboard: #{key}, Enter";function jn(o){var e=(/mac os x/i.test(navigator.userAgent)?"⌘":"Ctrl")+"+C";return o.replace(/#{\s*key\s*}/g,e)}function Wn(o,e){var t,i,n,r,s,l,a=!1;e||(e={}),t=e.debug||!1;try{n=Bn(),r=document.createRange(),s=document.getSelection(),l=document.createElement("span"),l.textContent=o,l.style.all="unset",l.style.position="fixed",l.style.top=0,l.style.clip="rect(0, 0, 0, 0)",l.style.whiteSpace="pre",l.style.webkitUserSelect="text",l.style.MozUserSelect="text",l.style.msUserSelect="text",l.style.userSelect="text",l.addEventListener("copy",function(u){if(u.stopPropagation(),e.format)if(u.preventDefault(),typeof u.clipboardData>"u"){t&&console.warn("unable to use e.clipboardData"),t&&console.warn("trying IE specific stuff"),window.clipboardData.clearData();var p=So[e.format]||So.default;window.clipboardData.setData(p,o)}else u.clipboardData.clearData(),u.clipboardData.setData(e.format,o);e.onCopy&&(u.preventDefault(),e.onCopy(u.clipboardData))}),document.body.appendChild(l),r.selectNodeContents(l),s.addRange(r);var c=document.execCommand("copy");if(!c)throw new Error("copy command was unsuccessful");a=!0}catch(u){t&&console.error("unable to copy using execCommand: ",u),t&&console.warn("trying IE specific stuff");try{window.clipboardData.setData(e.format||"text",o),e.onCopy&&e.onCopy(window.clipboardData),a=!0}catch(p){t&&console.error("unable to copy using clipboardData: ",p),t&&console.error("falling back to prompt"),i=jn("message"in e?e.message:Hn),window.prompt(i,o)}}finally{s&&(typeof s.removeRange=="function"?s.removeRange(r):s.removeAllRanges()),l&&document.body.removeChild(l),n()}return a}const Ut=1e3,Ft=(o,e)=>{const t=Array.from(o.querySelectorAll(e.join(", "))),i=Array.from(o.querySelectorAll("*")).filter(n=>n.shadowRoot).flatMap(n=>Ft(n.shadowRoot,e));return[...t,...i]};let Eo=!1;const Te=(o,e)=>{Eo||(window.addEventListener("message",n=>{n.data==="validate-license"&&window.location.reload()},!1),Eo=!0);const t=o._overlayElement;if(t){if(t.shadowRoot){const n=t.shadowRoot.querySelector("slot:not([name])");if(n&&n.assignedElements().length>0){Te(n.assignedElements()[0],e);return}}Te(t,e);return}const i=e.messageHtml?e.messageHtml:`${e.message} <p>Component: ${e.product.name} ${e.product.version}</p>`.replace(/https:([^ ]*)/g,"<a href='https:$1'>https:$1</a>");o.isConnected&&(o.outerHTML=`<no-license style="display:flex;align-items:center;text-align:center;justify-content:center;"><div>${i}</div></no-license>`)},xe={},Co={},he={},Yo={},U=o=>`${o.name}_${o.version}`,To=o=>{const{cvdlName:e,version:t}=o.constructor,i={name:e,version:t},n=o.tagName.toLowerCase();xe[e]=xe[e]??[],xe[e].push(n);const r=he[U(i)];r&&setTimeout(()=>Te(o,r),Ut),he[U(i)]||Yo[U(i)]||Co[U(i)]||(Co[U(i)]=!0,window.Vaadin.devTools.checkLicense(i))},Gn=o=>{Yo[U(o)]=!0,console.debug("License check ok for",o)},Jo=o=>{const e=o.product.name;he[U(o.product)]=o,console.error("License check failed for",e);const t=xe[e];(t==null?void 0:t.length)>0&&Ft(document,t).forEach(i=>{setTimeout(()=>Te(i,he[U(o.product)]),Ut)})},qn=o=>{const e=o.message,t=o.product.name;o.messageHtml=`No license found. <a target=_blank onclick="javascript:window.open(this.href);return false;" href="${e}">Go here to start a trial or retrieve your license.</a>`,he[U(o.product)]=o,console.error("No license found when checking",t);const i=xe[t];(i==null?void 0:i.length)>0&&Ft(document,i).forEach(n=>{setTimeout(()=>Te(n,he[U(o.product)]),Ut)})},Kn=()=>{window.Vaadin.devTools.createdCvdlElements.forEach(o=>{To(o)}),window.Vaadin.devTools.createdCvdlElements={push:o=>{To(o)}}};var Yn=Object.defineProperty,Jn=Object.getOwnPropertyDescriptor,$=(o,e,t,i)=>{for(var n=i>1?void 0:i?Jn(e,t):e,r=o.length-1,s;r>=0;r--)(s=o[r])&&(n=(i?s(e,t,n):s(n))||n);return i&&n&&Yn(e,t,n),n};const Xo=class extends Object{constructor(o){super(),this.status="unavailable",o&&(this.webSocket=new WebSocket(o),this.webSocket.onmessage=e=>this.handleMessage(e),this.webSocket.onerror=e=>this.handleError(e),this.webSocket.onclose=e=>{this.status!=="error"&&this.setStatus("unavailable"),this.webSocket=void 0}),setInterval(()=>{this.webSocket&&self.status!=="error"&&this.status!=="unavailable"&&this.webSocket.send("")},Xo.HEARTBEAT_INTERVAL)}onHandshake(){}onReload(){}onConnectionError(o){}onStatusChange(o){}onMessage(o){console.error("Unknown message received from the live reload server:",o)}handleMessage(o){let e;try{e=JSON.parse(o.data)}catch(t){this.handleError(`[${t.name}: ${t.message}`);return}e.command==="hello"?(this.setStatus("active"),this.onHandshake()):e.command==="reload"?this.status==="active"&&this.onReload():e.command==="license-check-ok"?Gn(e.data):e.command==="license-check-failed"?Jo(e.data):e.command==="license-check-nokey"?qn(e.data):this.onMessage(e)}handleError(o){console.error(o),this.setStatus("error"),o instanceof Event&&this.webSocket?this.onConnectionError(`Error in WebSocket connection to ${this.webSocket.url}`):this.onConnectionError(o)}setActive(o){!o&&this.status==="active"?this.setStatus("inactive"):o&&this.status==="inactive"&&this.setStatus("active")}setStatus(o){this.status!==o&&(this.status=o,this.onStatusChange(o))}send(o,e){const t=JSON.stringify({command:o,data:e});this.webSocket?this.webSocket.readyState!==WebSocket.OPEN?this.webSocket.addEventListener("open",()=>this.webSocket.send(t)):this.webSocket.send(t):console.error(`Unable to send message ${o}. No websocket is available`)}setFeature(o,e){this.send("setFeature",{featureId:o,enabled:e})}sendTelemetry(o){this.send("reportTelemetry",{browserData:o})}sendLicenseCheck(o){this.send("checkLicense",o)}sendShowComponentCreateLocation(o){this.send("showComponentCreateLocation",o)}sendShowComponentAttachLocation(o){this.send("showComponentAttachLocation",o)}};let Be=Xo;Be.HEARTBEAT_INTERVAL=18e4;const Xn=b`
  .popup {
    width: auto;
    position: fixed;
    background-color: var(--dev-tools-background-color-active-blurred);
    color: var(--dev-tools-text-color-primary);
    padding: 0.1875rem 0.75rem 0.1875rem 1rem;
    background-clip: padding-box;
    border-radius: var(--dev-tools-border-radius);
    overflow: hidden;
    margin: 0.5rem;
    width: 30rem;
    max-width: calc(100% - 1rem);
    max-height: calc(100vh - 1rem);
    flex-shrink: 1;
    background-color: var(--dev-tools-background-color-active);
    color: var(--dev-tools-text-color);
    transition: var(--dev-tools-transition-duration);
    transform-origin: bottom right;
    display: flex;
    flex-direction: column;
    box-shadow: var(--dev-tools-box-shadow);
    outline: none;
  }
`,x=class extends te{constructor(){super(),this.expanded=!1,this.messages=[],this.notifications=[],this.frontendStatus="unavailable",this.javaStatus="unavailable",this.tabs=[{id:"log",title:"Log",render:this.renderLog,activate:this.activateLog},{id:"info",title:"Info",render:this.renderInfo},{id:"features",title:"Feature Flags",render:this.renderFeatures}],this.activeTab="log",this.serverInfo={flowVersion:"",vaadinVersion:"",javaVersion:"",osVersion:"",productName:""},this.features=[],this.unreadErrors=!1,this.componentPickActive=!1,this.nextMessageId=1,this.transitionDuration=0,window.Vaadin.Flow&&this.tabs.push({id:"code",title:"Code",render:this.renderCode})}static get styles(){return[b`
        :host {
          --dev-tools-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell,
            'Helvetica Neue', sans-serif;
          --dev-tools-font-family-monospace: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
            monospace;

          --dev-tools-font-size: 0.8125rem;
          --dev-tools-font-size-small: 0.75rem;

          --dev-tools-text-color: rgba(255, 255, 255, 0.8);
          --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
          --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
          --dev-tools-text-color-active: rgba(255, 255, 255, 1);

          --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
          --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
          --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

          --dev-tools-border-radius: 0.5rem;
          --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

          --dev-tools-blue-hsl: ${this.BLUE_HSL};
          --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
          --dev-tools-green-hsl: ${this.GREEN_HSL};
          --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
          --dev-tools-grey-hsl: ${this.GREY_HSL};
          --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
          --dev-tools-yellow-hsl: ${this.YELLOW_HSL};
          --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
          --dev-tools-red-hsl: ${this.RED_HSL};
          --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

          /* Needs to be in ms, used in JavaScript as well */
          --dev-tools-transition-duration: 180ms;

          all: initial;

          direction: ltr;
          cursor: default;
          font: normal 400 var(--dev-tools-font-size) / 1.125rem var(--dev-tools-font-family);
          color: var(--dev-tools-text-color);
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;

          position: fixed;
          z-index: 20000;
          pointer-events: none;
          bottom: 0;
          right: 0;
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
        }

        .dev-tools {
          pointer-events: auto;
          display: flex;
          align-items: center;
          position: fixed;
          z-index: inherit;
          right: 0.5rem;
          bottom: 0.5rem;
          min-width: 1.75rem;
          height: 1.75rem;
          max-width: 1.75rem;
          border-radius: 0.5rem;
          padding: 0.375rem;
          box-sizing: border-box;
          background-color: var(--dev-tools-background-color-inactive);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          white-space: nowrap;
          line-height: 1rem;
        }

        .dev-tools:hover,
        .dev-tools.active {
          background-color: var(--dev-tools-background-color-active);
          box-shadow: var(--dev-tools-box-shadow);
        }

        .dev-tools.active {
          max-width: calc(100% - 1rem);
        }

        .dev-tools .dev-tools-icon {
          flex: none;
          pointer-events: none;
          display: inline-block;
          width: 1rem;
          height: 1rem;
          fill: #fff;
          transition: var(--dev-tools-transition-duration);
          margin: 0;
        }

        .dev-tools.active .dev-tools-icon {
          opacity: 0;
          position: absolute;
          transform: scale(0.5);
        }

        .dev-tools .status-blip {
          flex: none;
          display: block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          z-index: 20001;
          background: var(--dev-tools-grey-color);
          position: absolute;
          top: -1px;
          right: -1px;
        }

        .dev-tools .status-description {
          overflow: hidden;
          text-overflow: ellipsis;
          padding: 0 0.25rem;
        }

        .dev-tools.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.15);
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }

        .switch {
          display: inline-flex;
          align-items: center;
        }

        .switch input {
          opacity: 0;
          width: 0;
          height: 0;
          position: absolute;
        }

        .switch .slider {
          display: block;
          flex: none;
          width: 28px;
          height: 18px;
          border-radius: 9px;
          background-color: rgba(255, 255, 255, 0.3);
          transition: var(--dev-tools-transition-duration);
          margin-right: 0.5rem;
        }

        .switch:focus-within .slider,
        .switch .slider:hover {
          background-color: rgba(255, 255, 255, 0.35);
          transition: none;
        }

        .switch input:focus-visible ~ .slider {
          box-shadow: 0 0 0 2px var(--dev-tools-background-color-active), 0 0 0 4px var(--dev-tools-blue-color);
        }

        .switch .slider::before {
          content: '';
          display: block;
          margin: 2px;
          width: 14px;
          height: 14px;
          background-color: #fff;
          transition: var(--dev-tools-transition-duration);
          border-radius: 50%;
        }

        .switch input:checked + .slider {
          background-color: var(--dev-tools-green-color);
        }

        .switch input:checked + .slider::before {
          transform: translateX(10px);
        }

        .switch input:disabled + .slider::before {
          background-color: var(--dev-tools-grey-color);
        }

        .window.hidden {
          opacity: 0;
          transform: scale(0);
          position: absolute;
        }

        .window.visible {
          transform: none;
          opacity: 1;
          pointer-events: auto;
        }

        .window.visible ~ .dev-tools {
          opacity: 0;
          pointer-events: none;
        }

        .window.visible ~ .dev-tools .dev-tools-icon,
        .window.visible ~ .dev-tools .status-blip {
          transition: none;
          opacity: 0;
        }

        .window {
          border-radius: var(--dev-tools-border-radius);
          overflow: hidden;
          margin: 0.5rem;
          width: 30rem;
          max-width: calc(100% - 1rem);
          max-height: calc(100vh - 1rem);
          flex-shrink: 1;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          display: flex;
          flex-direction: column;
          box-shadow: var(--dev-tools-box-shadow);
          outline: none;
        }

        .window-toolbar {
          display: flex;
          flex: none;
          align-items: center;
          padding: 0.375rem;
          white-space: nowrap;
          order: 1;
          background-color: rgba(0, 0, 0, 0.2);
          gap: 0.5rem;
        }

        .tab {
          color: var(--dev-tools-text-color-secondary);
          font: inherit;
          font-size: var(--dev-tools-font-size-small);
          font-weight: 500;
          line-height: 1;
          padding: 0.25rem 0.375rem;
          background: none;
          border: none;
          margin: 0;
          border-radius: 0.25rem;
          transition: var(--dev-tools-transition-duration);
        }

        .tab:hover,
        .tab.active {
          color: var(--dev-tools-text-color-active);
        }

        .tab.active {
          background-color: rgba(255, 255, 255, 0.12);
        }

        .tab.unreadErrors::after {
          content: '•';
          color: hsl(var(--dev-tools-red-hsl));
          font-size: 1.5rem;
          position: absolute;
          transform: translate(0, -50%);
        }

        .ahreflike {
          font-weight: 500;
          color: var(--dev-tools-text-color-secondary);
          text-decoration: underline;
          cursor: pointer;
        }

        .ahreflike:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .button {
          all: initial;
          font-family: inherit;
          font-size: var(--dev-tools-font-size-small);
          line-height: 1;
          white-space: nowrap;
          background-color: rgba(0, 0, 0, 0.2);
          color: inherit;
          font-weight: 600;
          padding: 0.25rem 0.375rem;
          border-radius: 0.25rem;
        }

        .button:focus,
        .button:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .minimize-button {
          flex: none;
          width: 1rem;
          height: 1rem;
          color: inherit;
          background-color: transparent;
          border: 0;
          padding: 0;
          margin: 0 0 0 auto;
          opacity: 0.8;
        }

        .minimize-button:hover {
          opacity: 1;
        }

        .minimize-button svg {
          max-width: 100%;
        }

        .message.information {
          --dev-tools-notification-color: var(--dev-tools-blue-color);
        }

        .message.warning {
          --dev-tools-notification-color: var(--dev-tools-yellow-color);
        }

        .message.error {
          --dev-tools-notification-color: var(--dev-tools-red-color);
        }

        .message {
          display: flex;
          padding: 0.1875rem 0.75rem 0.1875rem 2rem;
          background-clip: padding-box;
        }

        .message.log {
          padding-left: 0.75rem;
        }

        .message-content {
          margin-right: 0.5rem;
          -webkit-user-select: text;
          -moz-user-select: text;
          user-select: text;
        }

        .message-heading {
          position: relative;
          display: flex;
          align-items: center;
          margin: 0.125rem 0;
        }

        .message.log {
          color: var(--dev-tools-text-color-secondary);
        }

        .message:not(.log) .message-heading {
          font-weight: 500;
        }

        .message.has-details .message-heading {
          color: var(--dev-tools-text-color-emphasis);
          font-weight: 600;
        }

        .message-heading::before {
          position: absolute;
          margin-left: -1.5rem;
          display: inline-block;
          text-align: center;
          font-size: 0.875em;
          font-weight: 600;
          line-height: calc(1.25em - 2px);
          width: 14px;
          height: 14px;
          box-sizing: border-box;
          border: 1px solid transparent;
          border-radius: 50%;
        }

        .message.information .message-heading::before {
          content: 'i';
          border-color: currentColor;
          color: var(--dev-tools-notification-color);
        }

        .message.warning .message-heading::before,
        .message.error .message-heading::before {
          content: '!';
          color: var(--dev-tools-background-color-active);
          background-color: var(--dev-tools-notification-color);
        }

        .features-tray {
          padding: 0.75rem;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .features-tray p {
          margin-top: 0;
          color: var(--dev-tools-text-color-secondary);
        }

        .features-tray .feature {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding-bottom: 0.5em;
        }

        .message .message-details {
          font-weight: 400;
          color: var(--dev-tools-text-color-secondary);
          margin: 0.25rem 0;
        }

        .message .message-details[hidden] {
          display: none;
        }

        .message .message-details p {
          display: inline;
          margin: 0;
          margin-right: 0.375em;
          word-break: break-word;
        }

        .message .persist {
          color: var(--dev-tools-text-color-secondary);
          white-space: nowrap;
          margin: 0.375rem 0;
          display: flex;
          align-items: center;
          position: relative;
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;
        }

        .message .persist::before {
          content: '';
          width: 1em;
          height: 1em;
          border-radius: 0.2em;
          margin-right: 0.375em;
          background-color: rgba(255, 255, 255, 0.3);
        }

        .message .persist:hover::before {
          background-color: rgba(255, 255, 255, 0.4);
        }

        .message .persist.on::before {
          background-color: rgba(255, 255, 255, 0.9);
        }

        .message .persist.on::after {
          content: '';
          order: -1;
          position: absolute;
          width: 0.75em;
          height: 0.25em;
          border: 2px solid var(--dev-tools-background-color-active);
          border-width: 0 0 2px 2px;
          transform: translate(0.05em, -0.05em) rotate(-45deg) scale(0.8, 0.9);
        }

        .message .dismiss-message {
          font-weight: 600;
          align-self: stretch;
          display: flex;
          align-items: center;
          padding: 0 0.25rem;
          margin-left: 0.5rem;
          color: var(--dev-tools-text-color-secondary);
        }

        .message .dismiss-message:hover {
          color: var(--dev-tools-text-color);
        }

        .notification-tray {
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
          margin: 0.5rem;
          flex: none;
        }

        .window.hidden + .notification-tray {
          margin-bottom: 3rem;
        }

        .notification-tray .message {
          pointer-events: auto;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          max-width: 30rem;
          box-sizing: border-box;
          border-radius: var(--dev-tools-border-radius);
          margin-top: 0.5rem;
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          animation: slideIn var(--dev-tools-transition-duration);
          box-shadow: var(--dev-tools-box-shadow);
          padding-top: 0.25rem;
          padding-bottom: 0.25rem;
        }

        .notification-tray .message.animate-out {
          animation: slideOut forwards var(--dev-tools-transition-duration);
        }

        .notification-tray .message .message-details {
          max-height: 10em;
          overflow: hidden;
        }

        .message-tray {
          flex: auto;
          overflow: auto;
          max-height: 20rem;
          user-select: text;
        }

        .message-tray .message {
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          padding-left: 2.25rem;
        }

        .message-tray .message.warning {
          background-color: hsla(var(--dev-tools-yellow-hsl), 0.09);
        }

        .message-tray .message.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.09);
        }

        .message-tray .message.error .message-heading {
          color: hsl(var(--dev-tools-red-hsl));
        }

        .message-tray .message.warning .message-heading {
          color: hsl(var(--dev-tools-yellow-hsl));
        }

        .message-tray .message + .message {
          border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .message-tray .dismiss-message,
        .message-tray .persist {
          display: none;
        }

        .info-tray {
          padding: 0.75rem;
          position: relative;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .info-tray dl {
          margin: 0;
          display: grid;
          grid-template-columns: max-content 1fr;
          column-gap: 0.75rem;
          position: relative;
        }

        .info-tray dt {
          grid-column: 1;
          color: var(--dev-tools-text-color-emphasis);
        }

        .info-tray dt:not(:first-child)::before {
          content: '';
          width: 100%;
          position: absolute;
          height: 1px;
          background-color: rgba(255, 255, 255, 0.1);
          margin-top: -0.375rem;
        }

        .info-tray dd {
          grid-column: 2;
          margin: 0;
        }

        .info-tray :is(dt, dd):not(:last-child) {
          margin-bottom: 0.75rem;
        }

        .info-tray dd + dd {
          margin-top: -0.5rem;
        }

        .info-tray .live-reload-status::before {
          content: '•';
          color: var(--status-color);
          width: 0.75rem;
          display: inline-block;
          font-size: 1rem;
          line-height: 0.5rem;
        }

        .info-tray .copy {
          position: fixed;
          z-index: 1;
          top: 0.5rem;
          right: 0.5rem;
        }

        .info-tray .switch {
          vertical-align: -4px;
        }

        @keyframes slideIn {
          from {
            transform: translateX(100%);
            opacity: 0;
          }
          to {
            transform: translateX(0%);
            opacity: 1;
          }
        }

        @keyframes slideOut {
          from {
            transform: translateX(0%);
            opacity: 1;
          }
          to {
            transform: translateX(100%);
            opacity: 0;
          }
        }

        @keyframes fade-in {
          0% {
            opacity: 0;
          }
        }

        @keyframes bounce {
          0% {
            transform: scale(0.8);
          }
          50% {
            transform: scale(1.5);
            background-color: hsla(var(--dev-tools-red-hsl), 1);
          }
          100% {
            transform: scale(1);
          }
        }

        @supports (backdrop-filter: blur(1px)) {
          .dev-tools,
          .window,
          .notification-tray .message {
            backdrop-filter: blur(8px);
          }
          .dev-tools:hover,
          .dev-tools.active,
          .window,
          .notification-tray .message {
            background-color: var(--dev-tools-background-color-active-blurred);
          }
        }
      `,Xn]}static get isActive(){const o=window.sessionStorage.getItem(x.ACTIVE_KEY_IN_SESSION_STORAGE);return o===null||o!=="false"}static notificationDismissed(o){const e=window.localStorage.getItem(x.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);return e!==null&&e.includes(o)}elementTelemetry(){let o={};try{const e=localStorage.getItem("vaadin.statistics.basket");if(!e)return;o=JSON.parse(e)}catch{return}this.frontendConnection&&this.frontendConnection.sendTelemetry(o)}openWebSocketConnection(){this.frontendStatus="unavailable",this.javaStatus="unavailable";const o=s=>this.log("error",s),e=()=>{if(this.liveReloadDisabled)return;this.showSplashMessage("Reloading…");const s=window.sessionStorage.getItem(x.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE),l=s?parseInt(s,10)+1:1;window.sessionStorage.setItem(x.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE,l.toString()),window.sessionStorage.setItem(x.TRIGGERED_KEY_IN_SESSION_STORAGE,"true"),window.location.reload()},t=new Be(this.getDedicatedWebSocketUrl());t.onHandshake=()=>{this.log("log","Vaadin development mode initialized"),x.isActive||t.setActive(!1),this.elementTelemetry()},t.onConnectionError=o,t.onReload=e,t.onStatusChange=s=>{this.frontendStatus=s},t.onMessage=s=>{(s==null?void 0:s.command)==="serverInfo"?this.serverInfo=s.data:(s==null?void 0:s.command)==="featureFlags"?this.features=s.data.features:console.error("Unknown message from front-end connection:",JSON.stringify(s))},this.frontendConnection=t;let i;this.backend===x.SPRING_BOOT_DEVTOOLS&&this.springBootLiveReloadPort?(i=new Be(this.getSpringBootWebSocketUrl(window.location)),i.onHandshake=()=>{x.isActive||i.setActive(!1)},i.onReload=e,i.onConnectionError=o):this.backend===x.JREBEL||this.backend===x.HOTSWAP_AGENT?i=t:i=new Be(void 0);const n=i.onStatusChange;i.onStatusChange=s=>{n(s),this.javaStatus=s};const r=i.onHandshake;i.onHandshake=()=>{r(),this.backend&&this.log("information",`Java live reload available: ${x.BACKEND_DISPLAY_NAME[this.backend]}`)},this.javaConnection=i,this.backend||this.showNotification("warning","Java live reload unavailable","Live reload for Java changes is currently not set up. Find out how to make use of this functionality to boost your workflow.","https://vaadin.com/docs/latest/flow/configuration/live-reload","liveReloadUnavailable")}getDedicatedWebSocketUrl(){function o(t){const i=document.createElement("div");return i.innerHTML=`<a href="${t}"/>`,i.firstChild.href}if(this.url===void 0)return;const e=o(this.url);if(!e.startsWith("http://")&&!e.startsWith("https://")){console.error("The protocol of the url should be http or https for live reload to work.");return}return`${e.replace(/^http/,"ws")}?v-r=push&debug_window`}getSpringBootWebSocketUrl(o){const{hostname:e}=o,t=o.protocol==="https:"?"wss":"ws";if(e.endsWith("gitpod.io")){const i=e.replace(/.*?-/,"");return`${t}://${this.springBootLiveReloadPort}-${i}`}else return`${t}://${e}:${this.springBootLiveReloadPort}`}connectedCallback(){if(super.connectedCallback(),this.catchErrors(),this.disableEventListener=t=>this.demoteSplashMessage(),document.body.addEventListener("focus",this.disableEventListener),document.body.addEventListener("click",this.disableEventListener),this.openWebSocketConnection(),window.sessionStorage.getItem(x.TRIGGERED_KEY_IN_SESSION_STORAGE)){const t=new Date,i=`${`0${t.getHours()}`.slice(-2)}:${`0${t.getMinutes()}`.slice(-2)}:${`0${t.getSeconds()}`.slice(-2)}`;this.showSplashMessage(`Page reloaded at ${i}`),window.sessionStorage.removeItem(x.TRIGGERED_KEY_IN_SESSION_STORAGE)}this.transitionDuration=parseInt(window.getComputedStyle(this).getPropertyValue("--dev-tools-transition-duration"),10);const e=window;e.Vaadin=e.Vaadin||{},e.Vaadin.devTools=Object.assign(this,e.Vaadin.devTools),Kn()}format(o){return o.toString()}catchErrors(){const o=window.Vaadin.ConsoleErrors;o&&o.forEach(e=>{this.log("error",e.map(t=>this.format(t)).join(" "))}),window.Vaadin.ConsoleErrors={push:e=>{this.log("error",e.map(t=>this.format(t)).join(" "))}}}disconnectedCallback(){this.disableEventListener&&(document.body.removeEventListener("focus",this.disableEventListener),document.body.removeEventListener("click",this.disableEventListener)),super.disconnectedCallback()}toggleExpanded(){this.notifications.slice().forEach(o=>this.dismissNotification(o.id)),this.expanded=!this.expanded,this.expanded&&this.root.focus()}showSplashMessage(o){this.splashMessage=o,this.splashMessage&&(this.expanded?this.demoteSplashMessage():setTimeout(()=>{this.demoteSplashMessage()},x.AUTO_DEMOTE_NOTIFICATION_DELAY))}demoteSplashMessage(){this.splashMessage&&this.log("log",this.splashMessage),this.showSplashMessage(void 0)}checkLicense(o){this.frontendConnection?this.frontendConnection.sendLicenseCheck(o):Jo({message:"Internal error: no connection",product:o})}log(o,e,t,i){const n=this.nextMessageId;for(this.nextMessageId+=1,this.messages.push({id:n,type:o,message:e,details:t,link:i,dontShowAgain:!1,deleted:!1});this.messages.length>x.MAX_LOG_ROWS;)this.messages.shift();this.requestUpdate(),this.updateComplete.then(()=>{const r=this.renderRoot.querySelector(".message-tray .message:last-child");this.expanded&&r?(setTimeout(()=>r.scrollIntoView({behavior:"smooth"}),this.transitionDuration),this.unreadErrors=!1):o==="error"&&(this.unreadErrors=!0)})}showNotification(o,e,t,i,n){if(n===void 0||!x.notificationDismissed(n)){if(this.notifications.filter(l=>l.persistentId===n).filter(l=>!l.deleted).length>0)return;const s=this.nextMessageId;this.nextMessageId+=1,this.notifications.push({id:s,type:o,message:e,details:t,link:i,persistentId:n,dontShowAgain:!1,deleted:!1}),i===void 0&&setTimeout(()=>{this.dismissNotification(s)},x.AUTO_DEMOTE_NOTIFICATION_DELAY),this.requestUpdate()}else this.log(o,e,t,i)}dismissNotification(o){const e=this.findNotificationIndex(o);if(e!==-1&&!this.notifications[e].deleted){const t=this.notifications[e];if(t.dontShowAgain&&t.persistentId&&!x.notificationDismissed(t.persistentId)){let i=window.localStorage.getItem(x.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);i=i===null?t.persistentId:`${i},${t.persistentId}`,window.localStorage.setItem(x.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE,i)}t.deleted=!0,this.log(t.type,t.message,t.details,t.link),setTimeout(()=>{const i=this.findNotificationIndex(o);i!==-1&&(this.notifications.splice(i,1),this.requestUpdate())},this.transitionDuration)}}findNotificationIndex(o){let e=-1;return this.notifications.some((t,i)=>t.id===o?(e=i,!0):!1),e}toggleDontShowAgain(o){const e=this.findNotificationIndex(o);if(e!==-1&&!this.notifications[e].deleted){const t=this.notifications[e];t.dontShowAgain=!t.dontShowAgain,this.requestUpdate()}}setActive(o){var e,t;(e=this.frontendConnection)==null||e.setActive(o),(t=this.javaConnection)==null||t.setActive(o),window.sessionStorage.setItem(x.ACTIVE_KEY_IN_SESSION_STORAGE,o?"true":"false")}getStatusColor(o){return o==="active"?b`hsl(${x.GREEN_HSL})`:o==="inactive"?b`hsl(${x.GREY_HSL})`:o==="unavailable"?b`hsl(${x.YELLOW_HSL})`:o==="error"?b`hsl(${x.RED_HSL})`:b`none`}renderMessage(o){return R`
      <div
        class="message ${o.type} ${o.deleted?"animate-out":""} ${o.details||o.link?"has-details":""}"
      >
        <div class="message-content">
          <div class="message-heading">${o.message}</div>
          <div class="message-details" ?hidden="${!o.details&&!o.link}">
            ${o.details?R`<p>${o.details}</p>`:""}
            ${o.link?R`<a class="ahreflike" href="${o.link}" target="_blank">Learn more</a>`:""}
          </div>
          ${o.persistentId?R`<div
                class="persist ${o.dontShowAgain?"on":"off"}"
                @click=${()=>this.toggleDontShowAgain(o.id)}
              >
                Don’t show again
              </div>`:""}
        </div>
        <div class="dismiss-message" @click=${()=>this.dismissNotification(o.id)}>Dismiss</div>
      </div>
    `}render(){return R` <div
        class="window ${this.expanded&&!this.componentPickActive?"visible":"hidden"}"
        tabindex="0"
        @keydown=${o=>o.key==="Escape"&&this.expanded&&this.toggleExpanded()}
      >
        <div class="window-toolbar">
          ${this.tabs.map(o=>R`<button
                class=${Ko({tab:!0,active:this.activeTab===o.id,unreadErrors:o.id==="log"&&this.unreadErrors})}
                id="${o.id}"
                @click=${()=>{this.activeTab=o.id,o.activate&&o.activate.call(this)}}
              >
                ${o.title}
              </button> `)}
          <button class="minimize-button" title="Minimize" @click=${()=>this.toggleExpanded()}>
            <svg fill="none" height="16" viewBox="0 0 16 16" width="16" xmlns="http://www.w3.org/2000/svg">
              <g fill="#fff" opacity=".8">
                <path
                  d="m7.25 1.75c0-.41421.33579-.75.75-.75h3.25c2.0711 0 3.75 1.67893 3.75 3.75v6.5c0 2.0711-1.6789 3.75-3.75 3.75h-6.5c-2.07107 0-3.75-1.6789-3.75-3.75v-3.25c0-.41421.33579-.75.75-.75s.75.33579.75.75v3.25c0 1.2426 1.00736 2.25 2.25 2.25h6.5c1.2426 0 2.25-1.0074 2.25-2.25v-6.5c0-1.24264-1.0074-2.25-2.25-2.25h-3.25c-.41421 0-.75-.33579-.75-.75z"
                />
                <path
                  d="m2.96967 2.96967c.29289-.29289.76777-.29289 1.06066 0l5.46967 5.46967v-2.68934c0-.41421.33579-.75.75-.75.4142 0 .75.33579.75.75v4.5c0 .4142-.3358.75-.75.75h-4.5c-.41421 0-.75-.3358-.75-.75 0-.41421.33579-.75.75-.75h2.68934l-5.46967-5.46967c-.29289-.29289-.29289-.76777 0-1.06066z"
                />
              </g>
            </svg>
          </button>
        </div>
        ${this.tabs.map(o=>this.activeTab===o.id?o.render.call(this):S)}
      </div>

      <div class="notification-tray">${this.notifications.map(o=>this.renderMessage(o))}</div>
      <vaadin-dev-tools-component-picker
        .active=${this.componentPickActive}
        @component-picker-pick=${o=>{const e=o.detail.component;this.renderRoot.querySelector("#locationType").value==="create"?this.frontendConnection.sendShowComponentCreateLocation(e):this.frontendConnection.sendShowComponentAttachLocation(e),this.componentPickActive=!1}}
        @component-picker-abort=${o=>{this.componentPickActive=!1}}
      ></vaadin-dev-tools-component-picker>
      <div
        class="dev-tools ${this.splashMessage?"active":""}${this.unreadErrors?" error":""}"
        @click=${()=>this.toggleExpanded()}
      >
        ${this.unreadErrors?R`<svg
              fill="none"
              height="16"
              viewBox="0 0 16 16"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              class="dev-tools-icon error"
            >
              <clipPath id="a"><path d="m0 0h16v16h-16z" /></clipPath>
              <g clip-path="url(#a)">
                <path
                  d="m6.25685 2.09894c.76461-1.359306 2.72169-1.359308 3.4863 0l5.58035 9.92056c.7499 1.3332-.2135 2.9805-1.7432 2.9805h-11.1606c-1.529658 0-2.4930857-1.6473-1.743156-2.9805z"
                  fill="#ff5c69"
                />
                <path
                  d="m7.99699 4c-.45693 0-.82368.37726-.81077.834l.09533 3.37352c.01094.38726.32803.69551.71544.69551.38741 0 .70449-.30825.71544-.69551l.09533-3.37352c.0129-.45674-.35384-.834-.81077-.834zm.00301 8c.60843 0 1-.3879 1-.979 0-.5972-.39157-.9851-1-.9851s-1 .3879-1 .9851c0 .5911.39157.979 1 .979z"
                  fill="#fff"
                />
              </g>
            </svg>`:R`<svg
              fill="none"
              height="17"
              viewBox="0 0 16 17"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              class="dev-tools-icon logo"
            >
              <g fill="#fff">
                <path
                  d="m8.88273 5.97926c0 .04401-.0032.08898-.00801.12913-.02467.42848-.37813.76767-.8117.76767-.43358 0-.78704-.34112-.81171-.76928-.00481-.04015-.00801-.08351-.00801-.12752 0-.42784-.10255-.87656-1.14434-.87656h-3.48364c-1.57118 0-2.315271-.72849-2.315271-2.21758v-1.26683c0-.42431.324618-.768314.748261-.768314.42331 0 .74441.344004.74441.768314v.42784c0 .47924.39576.81265 1.11293.81265h3.41538c1.5542 0 1.67373 1.156 1.725 1.7679h.03429c.05095-.6119.17048-1.7679 1.72468-1.7679h3.4154c.7172 0 1.0145-.32924 1.0145-.80847l-.0067-.43202c0-.42431.3227-.768314.7463-.768314.4234 0 .7255.344004.7255.768314v1.26683c0 1.48909-.6181 2.21758-2.1893 2.21758h-3.4836c-1.04182 0-1.14437.44872-1.14437.87656z"
                />
                <path
                  d="m8.82577 15.1648c-.14311.3144-.4588.5335-.82635.5335-.37268 0-.69252-.2249-.83244-.5466-.00206-.0037-.00412-.0073-.00617-.0108-.00275-.0047-.00549-.0094-.00824-.0145l-3.16998-5.87318c-.08773-.15366-.13383-.32816-.13383-.50395 0-.56168.45592-1.01879 1.01621-1.01879.45048 0 .75656.22069.96595.6993l2.16882 4.05042 2.17166-4.05524c.2069-.47379.513-.69448.9634-.69448.5603 0 1.0166.45711 1.0166 1.01879 0 .17579-.0465.35029-.1348.50523l-3.1697 5.8725c-.00503.0096-.01006.0184-.01509.0272-.00201.0036-.00402.0071-.00604.0106z"
                />
              </g>
            </svg>`}

        <span
          class="status-blip"
          style="background: linear-gradient(to right, ${this.getStatusColor(this.frontendStatus)} 50%, ${this.getStatusColor(this.javaStatus)} 50%)"
        ></span>
        ${this.splashMessage?R`<span class="status-description">${this.splashMessage}</span></div>`:S}
      </div>`}renderLog(){return R`<div class="message-tray">${this.messages.map(o=>this.renderMessage(o))}</div>`}activateLog(){this.unreadErrors=!1,this.updateComplete.then(()=>{const o=this.renderRoot.querySelector(".message-tray .message:last-child");o&&o.scrollIntoView()})}renderCode(){return R`<div class="info-tray">
      <div>
        <select id="locationType">
          <option value="create" selected>Create</option>
          <option value="attach">Attach</option>
        </select>
        <button
          class="button pick"
          @click=${()=>{this.componentPickActive=!0,He(()=>import("./component-picker-e1964d99.js"),[],import.meta.url)}}
        >
          Find component in code
        </button>
      </div>
      </div>
    </div>`}renderInfo(){return R`<div class="info-tray">
      <button class="button copy" @click=${this.copyInfoToClipboard}>Copy</button>
      <dl>
        <dt>${this.serverInfo.productName}</dt>
        <dd>${this.serverInfo.vaadinVersion}</dd>
        <dt>Flow</dt>
        <dd>${this.serverInfo.flowVersion}</dd>
        <dt>Java</dt>
        <dd>${this.serverInfo.javaVersion}</dd>
        <dt>OS</dt>
        <dd>${this.serverInfo.osVersion}</dd>
        <dt>Browser</dt>
        <dd>${navigator.userAgent}</dd>
        <dt>
          Live reload
          <label class="switch">
            <input
              id="toggle"
              type="checkbox"
              ?disabled=${this.liveReloadDisabled||(this.frontendStatus==="unavailable"||this.frontendStatus==="error")&&(this.javaStatus==="unavailable"||this.javaStatus==="error")}
              ?checked="${this.frontendStatus==="active"||this.javaStatus==="active"}"
              @change=${o=>this.setActive(o.target.checked)}
            />
            <span class="slider"></span>
          </label>
        </dt>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.javaStatus)}">
          Java ${this.javaStatus} ${this.backend?`(${x.BACKEND_DISPLAY_NAME[this.backend]})`:""}
        </dd>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.frontendStatus)}">
          Front end ${this.frontendStatus}
        </dd>
      </dl>
    </div>`}renderFeatures(){return R`<div class="features-tray">
      ${this.features.map(o=>R`<div class="feature">
          <label class="switch">
            <input
              class="feature-toggle"
              id="feature-toggle-${o.id}"
              type="checkbox"
              ?checked=${o.enabled}
              @change=${e=>this.toggleFeatureFlag(e,o)}
            />
            <span class="slider"></span>
            ${o.title}
          </label>
          <a class="ahreflike" href="${o.moreInfoLink}" target="_blank">Learn more</a>
        </div>`)}
    </div>`}copyInfoToClipboard(){const o=this.renderRoot.querySelectorAll(".info-tray dt, .info-tray dd"),e=Array.from(o).map(t=>(t.localName==="dd"?": ":`
`)+t.textContent.trim()).join("").replace(/^\n/,"");Wn(e),this.showNotification("information","Environment information copied to clipboard",void 0,void 0,"versionInfoCopied")}toggleFeatureFlag(o,e){const t=o.target.checked;this.frontendConnection?(this.frontendConnection.setFeature(e.id,t),this.showNotification("information",`“${e.title}” ${t?"enabled":"disabled"}`,e.requiresServerRestart?"This feature requires a server restart":void 0,void 0,`feature${e.id}${t?"Enabled":"Disabled"}`)):this.log("error",`Unable to toggle feature ${e.title}: No server connection available`)}};let y=x;y.BLUE_HSL=b`206, 100%, 70%`;y.GREEN_HSL=b`145, 80%, 42%`;y.GREY_HSL=b`0, 0%, 50%`;y.YELLOW_HSL=b`38, 98%, 64%`;y.RED_HSL=b`355, 100%, 68%`;y.MAX_LOG_ROWS=1e3;y.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE="vaadin.live-reload.dismissedNotifications";y.ACTIVE_KEY_IN_SESSION_STORAGE="vaadin.live-reload.active";y.TRIGGERED_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggered";y.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggeredCount";y.AUTO_DEMOTE_NOTIFICATION_DELAY=5e3;y.HOTSWAP_AGENT="HOTSWAP_AGENT";y.JREBEL="JREBEL";y.SPRING_BOOT_DEVTOOLS="SPRING_BOOT_DEVTOOLS";y.BACKEND_DISPLAY_NAME={HOTSWAP_AGENT:"HotswapAgent",JREBEL:"JRebel",SPRING_BOOT_DEVTOOLS:"Spring Boot Devtools"};$([_({type:String})],y.prototype,"url",2);$([_({type:Boolean,attribute:!0})],y.prototype,"liveReloadDisabled",2);$([_({type:String})],y.prototype,"backend",2);$([_({type:Number})],y.prototype,"springBootLiveReloadPort",2);$([_({type:Boolean,attribute:!1})],y.prototype,"expanded",2);$([_({type:Array,attribute:!1})],y.prototype,"messages",2);$([_({type:String,attribute:!1})],y.prototype,"splashMessage",2);$([_({type:Array,attribute:!1})],y.prototype,"notifications",2);$([_({type:String,attribute:!1})],y.prototype,"frontendStatus",2);$([_({type:String,attribute:!1})],y.prototype,"javaStatus",2);$([fe()],y.prototype,"tabs",2);$([fe()],y.prototype,"activeTab",2);$([fe()],y.prototype,"serverInfo",2);$([fe()],y.prototype,"features",2);$([fe()],y.prototype,"unreadErrors",2);$([In(".window")],y.prototype,"root",2);$([fe()],y.prototype,"componentPickActive",2);customElements.get("vaadin-dev-tools")===void 0&&customElements.define("vaadin-dev-tools",y);(function(){if(typeof document>"u"||"adoptedStyleSheets"in document)return;var o="ShadyCSS"in window&&!ShadyCSS.nativeShadow,e=document.implementation.createHTMLDocument(""),t=new WeakMap,i=typeof DOMException=="object"?Error:DOMException,n=Object.defineProperty,r=Array.prototype.forEach,s=/@import.+?;?$/gm;function l(d){var m=d.replace(s,"");return m!==d&&console.warn("@import rules are not allowed here. See https://github.com/WICG/construct-stylesheets/issues/119#issuecomment-588352418"),m.trim()}function a(d){return"isConnected"in d?d.isConnected:document.contains(d)}function c(d){return d.filter(function(m,v){return d.indexOf(m)===v})}function u(d,m){return d.filter(function(v){return m.indexOf(v)===-1})}function p(d){d.parentNode.removeChild(d)}function h(d){return d.shadowRoot||t.get(d)}var f=["addRule","deleteRule","insertRule","removeRule"],G=CSSStyleSheet,q=G.prototype;q.replace=function(){return Promise.reject(new i("Can't call replace on non-constructed CSSStyleSheets."))},q.replaceSync=function(){throw new i("Failed to execute 'replaceSync' on 'CSSStyleSheet': Can't call replaceSync on non-constructed CSSStyleSheets.")};function H(d){return typeof d=="object"?ne.isPrototypeOf(d)||q.isPrototypeOf(d):!1}function Ze(d){return typeof d=="object"?q.isPrototypeOf(d):!1}var A=new WeakMap,V=new WeakMap,oe=new WeakMap,ie=new WeakMap;function et(d,m){var v=document.createElement("style");return oe.get(d).set(m,v),V.get(d).push(m),v}function j(d,m){return oe.get(d).get(m)}function Re(d,m){oe.get(d).delete(m),V.set(d,V.get(d).filter(function(v){return v!==m}))}function Bt(d,m){requestAnimationFrame(function(){m.textContent=A.get(d).textContent,ie.get(d).forEach(function(v){return m.sheet[v.method].apply(m.sheet,v.args)})})}function Ne(d){if(!A.has(d))throw new TypeError("Illegal invocation")}function tt(){var d=this,m=document.createElement("style");e.body.appendChild(m),A.set(d,m),V.set(d,[]),oe.set(d,new WeakMap),ie.set(d,[])}var ne=tt.prototype;ne.replace=function(m){try{return this.replaceSync(m),Promise.resolve(this)}catch(v){return Promise.reject(v)}},ne.replaceSync=function(m){if(Ne(this),typeof m=="string"){var v=this;A.get(v).textContent=l(m),ie.set(v,[]),V.get(v).forEach(function(k){k.isConnected()&&Bt(v,j(v,k))})}},n(ne,"cssRules",{configurable:!0,enumerable:!0,get:function(){return Ne(this),A.get(this).sheet.cssRules}}),n(ne,"media",{configurable:!0,enumerable:!0,get:function(){return Ne(this),A.get(this).sheet.media}}),f.forEach(function(d){ne[d]=function(){var m=this;Ne(m);var v=arguments;ie.get(m).push({method:d,args:v}),V.get(m).forEach(function(N){if(N.isConnected()){var T=j(m,N).sheet;T[d].apply(T,v)}});var k=A.get(m).sheet;return k[d].apply(k,v)}}),n(tt,Symbol.hasInstance,{configurable:!0,value:H});var Ht={childList:!0,subtree:!0},jt=new WeakMap;function re(d){var m=jt.get(d);return m||(m=new qt(d),jt.set(d,m)),m}function Wt(d){n(d.prototype,"adoptedStyleSheets",{configurable:!0,enumerable:!0,get:function(){return re(this).sheets},set:function(m){re(this).update(m)}})}function ot(d,m){for(var v=document.createNodeIterator(d,NodeFilter.SHOW_ELEMENT,function(N){return h(N)?NodeFilter.FILTER_ACCEPT:NodeFilter.FILTER_REJECT},null,!1),k=void 0;k=v.nextNode();)m(h(k))}var Le=new WeakMap,se=new WeakMap,Ie=new WeakMap;function di(d,m){return m instanceof HTMLStyleElement&&se.get(d).some(function(v){return j(v,d)})}function Gt(d){var m=Le.get(d);return m instanceof Document?m.body:m}function it(d){var m=document.createDocumentFragment(),v=se.get(d),k=Ie.get(d),N=Gt(d);k.disconnect(),v.forEach(function(T){m.appendChild(j(T,d)||et(T,d))}),N.insertBefore(m,null),k.observe(N,Ht),v.forEach(function(T){Bt(T,j(T,d))})}function qt(d){var m=this;m.sheets=[],Le.set(m,d),se.set(m,[]),Ie.set(m,new MutationObserver(function(v,k){if(!document){k.disconnect();return}v.forEach(function(N){o||r.call(N.addedNodes,function(T){T instanceof Element&&ot(T,function(ae){re(ae).connect()})}),r.call(N.removedNodes,function(T){T instanceof Element&&(di(m,T)&&it(m),o||ot(T,function(ae){re(ae).disconnect()}))})})}))}if(qt.prototype={isConnected:function(){var d=Le.get(this);return d instanceof Document?d.readyState!=="loading":a(d.host)},connect:function(){var d=Gt(this);Ie.get(this).observe(d,Ht),se.get(this).length>0&&it(this),ot(d,function(m){re(m).connect()})},disconnect:function(){Ie.get(this).disconnect()},update:function(d){var m=this,v=Le.get(m)===document?"Document":"ShadowRoot";if(!Array.isArray(d))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Iterator getter is not callable.");if(!d.every(H))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Failed to convert value to 'CSSStyleSheet'");if(d.some(Ze))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Can't adopt non-constructed stylesheets");m.sheets=d;var k=se.get(m),N=c(d),T=u(k,N);T.forEach(function(ae){p(j(ae,m)),Re(ae,m)}),se.set(m,N),m.isConnected()&&N.length>0&&it(m)}},window.CSSStyleSheet=tt,Wt(Document),"ShadowRoot"in window){Wt(ShadowRoot);var Kt=Element.prototype,ui=Kt.attachShadow;Kt.attachShadow=function(m){var v=ui.call(this,m);return m.mode==="closed"&&t.set(this,v),v}}var Ae=re(document);Ae.isConnected()?Ae.connect():document.addEventListener("DOMContentLoaded",Ae.connect.bind(Ae))})();/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Qn=o=>class extends o{static get properties(){return{_theme:{type:String,readOnly:!0}}}static get observedAttributes(){return[...super.observedAttributes,"theme"]}attributeChangedCallback(t,i,n){super.attributeChangedCallback(t,i,n),t==="theme"&&this._set_theme(n)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Qo=[];function Zo(o){return o&&Object.prototype.hasOwnProperty.call(o,"__themes")}function Zn(o){return Zo(customElements.get(o))}function er(o=[]){return[o].flat(1/0).filter(e=>e instanceof zt?!0:(console.warn("An item in styles is not of type CSSResult. Use `unsafeCSS` or `css`."),!1))}function ke(o,e,t={}){o&&Zn(o)&&console.warn(`The custom element definition for "${o}"
      was finalized before a style module was registered.
      Make sure to add component specific style modules before
      importing the corresponding custom element.`),e=er(e),window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.registerStyles(o,e,t):Qo.push({themeFor:o,styles:e,include:t.include,moduleId:t.moduleId})}function It(){return window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.getAllThemes():Qo}function tr(o,e){return(o||"").split(" ").some(t=>new RegExp(`^${t.split("*").join(".*")}$`,"u").test(e))}function or(o=""){let e=0;return o.startsWith("lumo-")||o.startsWith("material-")?e=1:o.startsWith("vaadin-")&&(e=2),e}function ei(o){const e=[];return o.include&&[].concat(o.include).forEach(t=>{const i=It().find(n=>n.moduleId===t);i?e.push(...ei(i),...i.styles):console.warn(`Included moduleId ${t} not found in style registry`)},o.styles),e}function ir(o,e){const t=document.createElement("style");t.innerHTML=o.map(i=>i.cssText).join(`
`),e.content.appendChild(t)}function nr(o){const e=`${o}-default-theme`,t=It().filter(i=>i.moduleId!==e&&tr(i.themeFor,o)).map(i=>({...i,styles:[...ei(i),...i.styles],includePriority:or(i.moduleId)})).sort((i,n)=>n.includePriority-i.includePriority);return t.length>0?t:It().filter(i=>i.moduleId===e)}const Er=o=>class extends Qn(o){static finalize(){if(super.finalize(),this.elementStyles)return;const t=this.prototype._template;!t||Zo(this)||ir(this.getStylesForThis(),t)}static finalizeStyles(t){const i=this.getStylesForThis();return t?[...super.finalizeStyles(t),...i]:i}static getStylesForThis(){const t=Object.getPrototypeOf(this.prototype),i=(t?t.constructor.__themes:[])||[];this.__themes=[...i,...nr(this.is)];const n=this.__themes.flatMap(r=>r.styles);return n.filter((r,s)=>s===n.lastIndexOf(r))}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class rr extends HTMLElement{static get version(){return"24.0.2"}}customElements.define("vaadin-lumo-styles",rr);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const sr=b`
  :host {
    /* prettier-ignore */
    --lumo-font-family: -apple-system, BlinkMacSystemFont, 'Roboto', 'Segoe UI', Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';

    /* Font sizes */
    --lumo-font-size-xxs: 0.75rem;
    --lumo-font-size-xs: 0.8125rem;
    --lumo-font-size-s: 0.875rem;
    --lumo-font-size-m: 1rem;
    --lumo-font-size-l: 1.125rem;
    --lumo-font-size-xl: 1.375rem;
    --lumo-font-size-xxl: 1.75rem;
    --lumo-font-size-xxxl: 2.5rem;

    /* Line heights */
    --lumo-line-height-xs: 1.25;
    --lumo-line-height-s: 1.375;
    --lumo-line-height-m: 1.625;
  }
`,ti=b`
  body,
  :host {
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-m);
    line-height: var(--lumo-line-height-m);
    -webkit-text-size-adjust: 100%;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  small,
  [theme~='font-size-s'] {
    font-size: var(--lumo-font-size-s);
    line-height: var(--lumo-line-height-s);
  }

  [theme~='font-size-xs'] {
    font-size: var(--lumo-font-size-xs);
    line-height: var(--lumo-line-height-xs);
  }

  :where(h1, h2, h3, h4, h5, h6) {
    font-weight: 600;
    line-height: var(--lumo-line-height-xs);
    margin: 0;
  }

  :where(h1) {
    font-size: var(--lumo-font-size-xxxl);
  }

  :where(h2) {
    font-size: var(--lumo-font-size-xxl);
  }

  :where(h3) {
    font-size: var(--lumo-font-size-xl);
  }

  :where(h4) {
    font-size: var(--lumo-font-size-l);
  }

  :where(h5) {
    font-size: var(--lumo-font-size-m);
  }

  :where(h6) {
    font-size: var(--lumo-font-size-xs);
    text-transform: uppercase;
    letter-spacing: 0.03em;
  }

  p,
  blockquote {
    margin-top: 0.5em;
    margin-bottom: 0.75em;
  }

  a {
    text-decoration: none;
  }

  a:where(:any-link):hover {
    text-decoration: underline;
  }

  hr {
    display: block;
    align-self: stretch;
    height: 1px;
    border: 0;
    padding: 0;
    margin: var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2);
    background-color: var(--lumo-contrast-10pct);
  }

  blockquote {
    border-left: 2px solid var(--lumo-contrast-30pct);
  }

  b,
  strong {
    font-weight: 600;
  }

  /* RTL specific styles */
  blockquote[dir='rtl'] {
    border-left: none;
    border-right: 2px solid var(--lumo-contrast-30pct);
  }
`;ke("",ti,{moduleId:"lumo-typography"});const oi=document.createElement("template");oi.innerHTML=`<style>${sr.toString().replace(":host","html")}</style>`;document.head.appendChild(oi.content);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ar=b`
  :host {
    /* Base (background) */
    --lumo-base-color: #fff;

    /* Tint */
    --lumo-tint-5pct: hsla(0, 0%, 100%, 0.3);
    --lumo-tint-10pct: hsla(0, 0%, 100%, 0.37);
    --lumo-tint-20pct: hsla(0, 0%, 100%, 0.44);
    --lumo-tint-30pct: hsla(0, 0%, 100%, 0.5);
    --lumo-tint-40pct: hsla(0, 0%, 100%, 0.57);
    --lumo-tint-50pct: hsla(0, 0%, 100%, 0.64);
    --lumo-tint-60pct: hsla(0, 0%, 100%, 0.7);
    --lumo-tint-70pct: hsla(0, 0%, 100%, 0.77);
    --lumo-tint-80pct: hsla(0, 0%, 100%, 0.84);
    --lumo-tint-90pct: hsla(0, 0%, 100%, 0.9);
    --lumo-tint: #fff;

    /* Shade */
    --lumo-shade-5pct: hsla(214, 61%, 25%, 0.05);
    --lumo-shade-10pct: hsla(214, 57%, 24%, 0.1);
    --lumo-shade-20pct: hsla(214, 53%, 23%, 0.16);
    --lumo-shade-30pct: hsla(214, 50%, 22%, 0.26);
    --lumo-shade-40pct: hsla(214, 47%, 21%, 0.38);
    --lumo-shade-50pct: hsla(214, 45%, 20%, 0.52);
    --lumo-shade-60pct: hsla(214, 43%, 19%, 0.6);
    --lumo-shade-70pct: hsla(214, 42%, 18%, 0.69);
    --lumo-shade-80pct: hsla(214, 41%, 17%, 0.83);
    --lumo-shade-90pct: hsla(214, 40%, 16%, 0.94);
    --lumo-shade: hsl(214, 35%, 15%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-shade-5pct);
    --lumo-contrast-10pct: var(--lumo-shade-10pct);
    --lumo-contrast-20pct: var(--lumo-shade-20pct);
    --lumo-contrast-30pct: var(--lumo-shade-30pct);
    --lumo-contrast-40pct: var(--lumo-shade-40pct);
    --lumo-contrast-50pct: var(--lumo-shade-50pct);
    --lumo-contrast-60pct: var(--lumo-shade-60pct);
    --lumo-contrast-70pct: var(--lumo-shade-70pct);
    --lumo-contrast-80pct: var(--lumo-shade-80pct);
    --lumo-contrast-90pct: var(--lumo-shade-90pct);
    --lumo-contrast: var(--lumo-shade);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 100%, 48%);
    --lumo-primary-color-50pct: hsla(214, 100%, 49%, 0.76);
    --lumo-primary-color-10pct: hsla(214, 100%, 60%, 0.13);
    --lumo-primary-text-color: hsl(214, 100%, 43%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 85%, 48%);
    --lumo-error-color-50pct: hsla(3, 85%, 49%, 0.5);
    --lumo-error-color-10pct: hsla(3, 85%, 49%, 0.1);
    --lumo-error-text-color: hsl(3, 89%, 42%);
    --lumo-error-contrast-color: #fff;

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 72%, 31%, 0.5);
    --lumo-success-color-10pct: hsla(145, 72%, 31%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 25%);
    --lumo-success-contrast-color: #fff;
  }
`,ii=document.createElement("template");ii.innerHTML=`<style>${ar.toString().replace(":host","html")}</style>`;document.head.appendChild(ii.content);const ni=b`
  [theme~='dark'] {
    /* Base (background) */
    --lumo-base-color: hsl(214, 35%, 21%);

    /* Tint */
    --lumo-tint-5pct: hsla(214, 65%, 85%, 0.06);
    --lumo-tint-10pct: hsla(214, 60%, 80%, 0.14);
    --lumo-tint-20pct: hsla(214, 64%, 82%, 0.23);
    --lumo-tint-30pct: hsla(214, 69%, 84%, 0.32);
    --lumo-tint-40pct: hsla(214, 73%, 86%, 0.41);
    --lumo-tint-50pct: hsla(214, 78%, 88%, 0.5);
    --lumo-tint-60pct: hsla(214, 82%, 90%, 0.58);
    --lumo-tint-70pct: hsla(214, 87%, 92%, 0.69);
    --lumo-tint-80pct: hsla(214, 91%, 94%, 0.8);
    --lumo-tint-90pct: hsla(214, 96%, 96%, 0.9);
    --lumo-tint: hsl(214, 100%, 98%);

    /* Shade */
    --lumo-shade-5pct: hsla(214, 0%, 0%, 0.07);
    --lumo-shade-10pct: hsla(214, 4%, 2%, 0.15);
    --lumo-shade-20pct: hsla(214, 8%, 4%, 0.23);
    --lumo-shade-30pct: hsla(214, 12%, 6%, 0.32);
    --lumo-shade-40pct: hsla(214, 16%, 8%, 0.41);
    --lumo-shade-50pct: hsla(214, 20%, 10%, 0.5);
    --lumo-shade-60pct: hsla(214, 24%, 12%, 0.6);
    --lumo-shade-70pct: hsla(214, 28%, 13%, 0.7);
    --lumo-shade-80pct: hsla(214, 32%, 13%, 0.8);
    --lumo-shade-90pct: hsla(214, 33%, 13%, 0.9);
    --lumo-shade: hsl(214, 33%, 13%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-tint-5pct);
    --lumo-contrast-10pct: var(--lumo-tint-10pct);
    --lumo-contrast-20pct: var(--lumo-tint-20pct);
    --lumo-contrast-30pct: var(--lumo-tint-30pct);
    --lumo-contrast-40pct: var(--lumo-tint-40pct);
    --lumo-contrast-50pct: var(--lumo-tint-50pct);
    --lumo-contrast-60pct: var(--lumo-tint-60pct);
    --lumo-contrast-70pct: var(--lumo-tint-70pct);
    --lumo-contrast-80pct: var(--lumo-tint-80pct);
    --lumo-contrast-90pct: var(--lumo-tint-90pct);
    --lumo-contrast: var(--lumo-tint);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 90%, 48%);
    --lumo-primary-color-50pct: hsla(214, 90%, 70%, 0.69);
    --lumo-primary-color-10pct: hsla(214, 90%, 55%, 0.13);
    --lumo-primary-text-color: hsl(214, 90%, 77%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 79%, 49%);
    --lumo-error-color-50pct: hsla(3, 75%, 62%, 0.5);
    --lumo-error-color-10pct: hsla(3, 75%, 62%, 0.14);
    --lumo-error-text-color: hsl(3, 100%, 80%);

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 92%, 51%, 0.5);
    --lumo-success-color-10pct: hsla(145, 92%, 51%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 46%);
  }

  html {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: light;
  }

  [theme~='dark'] {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: dark;
  }

  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    color: var(--lumo-header-text-color);
  }

  a:where(:any-link) {
    color: var(--lumo-primary-text-color);
  }

  a:not(:any-link) {
    color: var(--lumo-disabled-text-color);
  }

  blockquote {
    color: var(--lumo-secondary-text-color);
  }

  code,
  pre {
    background-color: var(--lumo-contrast-10pct);
    border-radius: var(--lumo-border-radius-m);
  }
`;ke("",ni,{moduleId:"lumo-color"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ri=b`
  :host {
    /* Square */
    --lumo-space-xs: 0.25rem;
    --lumo-space-s: 0.5rem;
    --lumo-space-m: 1rem;
    --lumo-space-l: 1.5rem;
    --lumo-space-xl: 2.5rem;

    /* Wide */
    --lumo-space-wide-xs: calc(var(--lumo-space-xs) / 2) var(--lumo-space-xs);
    --lumo-space-wide-s: calc(var(--lumo-space-s) / 2) var(--lumo-space-s);
    --lumo-space-wide-m: calc(var(--lumo-space-m) / 2) var(--lumo-space-m);
    --lumo-space-wide-l: calc(var(--lumo-space-l) / 2) var(--lumo-space-l);
    --lumo-space-wide-xl: calc(var(--lumo-space-xl) / 2) var(--lumo-space-xl);

    /* Tall */
    --lumo-space-tall-xs: var(--lumo-space-xs) calc(var(--lumo-space-xs) / 2);
    --lumo-space-tall-s: var(--lumo-space-s) calc(var(--lumo-space-s) / 2);
    --lumo-space-tall-m: var(--lumo-space-m) calc(var(--lumo-space-m) / 2);
    --lumo-space-tall-l: var(--lumo-space-l) calc(var(--lumo-space-l) / 2);
    --lumo-space-tall-xl: var(--lumo-space-xl) calc(var(--lumo-space-xl) / 2);
  }
`,si=document.createElement("template");si.innerHTML=`<style>${ri.toString().replace(":host","html")}</style>`;document.head.appendChild(si.content);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const lr=b`
  :host {
    /* Border radius */
    --lumo-border-radius-s: 0.25em; /* Checkbox, badge, date-picker year indicator, etc */
    --lumo-border-radius-m: var(--lumo-border-radius, 0.25em); /* Button, text field, menu overlay, etc */
    --lumo-border-radius-l: 0.5em; /* Dialog, notification, etc */

    /* Shadow */
    --lumo-box-shadow-xs: 0 1px 4px -1px var(--lumo-shade-50pct);
    --lumo-box-shadow-s: 0 2px 4px -1px var(--lumo-shade-20pct), 0 3px 12px -1px var(--lumo-shade-30pct);
    --lumo-box-shadow-m: 0 2px 6px -1px var(--lumo-shade-20pct), 0 8px 24px -4px var(--lumo-shade-40pct);
    --lumo-box-shadow-l: 0 3px 18px -2px var(--lumo-shade-20pct), 0 12px 48px -6px var(--lumo-shade-40pct);
    --lumo-box-shadow-xl: 0 4px 24px -3px var(--lumo-shade-20pct), 0 18px 64px -8px var(--lumo-shade-40pct);

    /* Clickable element cursor */
    --lumo-clickable-cursor: default;
  }
`;b`
  html {
    --vaadin-checkbox-size: calc(var(--lumo-size-m) / 2);
    --vaadin-radio-button-size: calc(var(--lumo-size-m) / 2);
    --vaadin-input-field-border-radius: var(--lumo-border-radius-m);
  }
`;const ai=document.createElement("template");ai.innerHTML=`<style>${lr.toString().replace(":host","html")}$</style>`;document.head.appendChild(ai.content);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const li=b`
  [theme~='badge'] {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    padding: 0.4em calc(0.5em + var(--lumo-border-radius-s) / 4);
    color: var(--lumo-primary-text-color);
    background-color: var(--lumo-primary-color-10pct);
    border-radius: var(--lumo-border-radius-s);
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-s);
    line-height: 1;
    font-weight: 500;
    text-transform: initial;
    letter-spacing: initial;
    min-width: calc(var(--lumo-line-height-xs) * 1em + 0.45em);
    flex-shrink: 0;
  }

  /* Ensure proper vertical alignment */
  [theme~='badge']::before {
    display: inline-block;
    content: '\\2003';
    width: 0;
  }

  [theme~='badge'][theme~='small'] {
    font-size: var(--lumo-font-size-xxs);
    line-height: 1;
  }

  /* Colors */

  [theme~='badge'][theme~='success'] {
    color: var(--lumo-success-text-color);
    background-color: var(--lumo-success-color-10pct);
  }

  [theme~='badge'][theme~='error'] {
    color: var(--lumo-error-text-color);
    background-color: var(--lumo-error-color-10pct);
  }

  [theme~='badge'][theme~='contrast'] {
    color: var(--lumo-contrast-80pct);
    background-color: var(--lumo-contrast-5pct);
  }

  /* Primary */

  [theme~='badge'][theme~='primary'] {
    color: var(--lumo-primary-contrast-color);
    background-color: var(--lumo-primary-color);
  }

  [theme~='badge'][theme~='success'][theme~='primary'] {
    color: var(--lumo-success-contrast-color);
    background-color: var(--lumo-success-color);
  }

  [theme~='badge'][theme~='error'][theme~='primary'] {
    color: var(--lumo-error-contrast-color);
    background-color: var(--lumo-error-color);
  }

  [theme~='badge'][theme~='contrast'][theme~='primary'] {
    color: var(--lumo-base-color);
    background-color: var(--lumo-contrast);
  }

  /* Links */

  [theme~='badge'][href]:hover {
    text-decoration: none;
  }

  /* Icon */

  [theme~='badge'] vaadin-icon {
    margin: -0.25em 0;
  }

  [theme~='badge'] vaadin-icon:first-child {
    margin-left: -0.375em;
  }

  [theme~='badge'] vaadin-icon:last-child {
    margin-right: -0.375em;
  }

  vaadin-icon[theme~='badge'][icon] {
    min-width: 0;
    padding: 0;
    font-size: 1rem;
    width: var(--lumo-icon-size-m);
    height: var(--lumo-icon-size-m);
  }

  vaadin-icon[theme~='badge'][icon][theme~='small'] {
    width: var(--lumo-icon-size-s);
    height: var(--lumo-icon-size-s);
  }

  /* Empty */

  [theme~='badge']:not([icon]):empty {
    min-width: 0;
    width: 1em;
    height: 1em;
    padding: 0;
    border-radius: 50%;
    background-color: var(--lumo-primary-color);
  }

  [theme~='badge'][theme~='small']:not([icon]):empty {
    width: 0.75em;
    height: 0.75em;
  }

  [theme~='badge'][theme~='contrast']:not([icon]):empty {
    background-color: var(--lumo-contrast);
  }

  [theme~='badge'][theme~='success']:not([icon]):empty {
    background-color: var(--lumo-success-color);
  }

  [theme~='badge'][theme~='error']:not([icon]):empty {
    background-color: var(--lumo-error-color);
  }

  /* Pill */

  [theme~='badge'][theme~='pill'] {
    --lumo-border-radius-s: 1em;
  }

  /* RTL specific styles */

  [dir='rtl'][theme~='badge'] vaadin-icon:first-child {
    margin-right: -0.375em;
    margin-left: 0;
  }

  [dir='rtl'][theme~='badge'] vaadin-icon:last-child {
    margin-left: -0.375em;
    margin-right: 0;
  }
`;ke("",li,{moduleId:"lumo-badge"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const cr=b`
  /* === Screen readers === */
  .sr-only {
    border-width: 0;
    clip: rect(0, 0, 0, 0);
    height: 1px;
    margin: -1px;
    overflow: hidden;
    padding: 0;
    position: absolute;
    white-space: nowrap;
    width: 1px;
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const dr=b`
  /* === Background color === */
  .bg-base {
    background-color: var(--lumo-base-color);
  }

  .bg-transparent {
    background-color: transparent;
  }

  .bg-contrast-5 {
    background-color: var(--lumo-contrast-5pct);
  }
  .bg-contrast-10 {
    background-color: var(--lumo-contrast-10pct);
  }
  .bg-contrast-20 {
    background-color: var(--lumo-contrast-20pct);
  }
  .bg-contrast-30 {
    background-color: var(--lumo-contrast-30pct);
  }
  .bg-contrast-40 {
    background-color: var(--lumo-contrast-40pct);
  }
  .bg-contrast-50 {
    background-color: var(--lumo-contrast-50pct);
  }
  .bg-contrast-60 {
    background-color: var(--lumo-contrast-60pct);
  }
  .bg-contrast-70 {
    background-color: var(--lumo-contrast-70pct);
  }
  .bg-contrast-80 {
    background-color: var(--lumo-contrast-80pct);
  }
  .bg-contrast-90 {
    background-color: var(--lumo-contrast-90pct);
  }
  .bg-contrast {
    background-color: var(--lumo-contrast);
  }

  .bg-primary {
    background-color: var(--lumo-primary-color);
  }
  .bg-primary-50 {
    background-color: var(--lumo-primary-color-50pct);
  }
  .bg-primary-10 {
    background-color: var(--lumo-primary-color-10pct);
  }

  .bg-error {
    background-color: var(--lumo-error-color);
  }
  .bg-error-50 {
    background-color: var(--lumo-error-color-50pct);
  }
  .bg-error-10 {
    background-color: var(--lumo-error-color-10pct);
  }

  .bg-success {
    background-color: var(--lumo-success-color);
  }
  .bg-success-50 {
    background-color: var(--lumo-success-color-50pct);
  }
  .bg-success-10 {
    background-color: var(--lumo-success-color-10pct);
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ur=b`
  /* === Border === */
  .border-0 {
    border: none;
  }
  .border {
    border: 1px solid;
  }
  .border-b {
    border-bottom: 1px solid;
  }
  .border-l {
    border-left: 1px solid;
  }
  .border-r {
    border-right: 1px solid;
  }
  .border-t {
    border-top: 1px solid;
  }

  /* === Border color === */
  .border-contrast-5 {
    border-color: var(--lumo-contrast-5pct);
  }
  .border-contrast-10 {
    border-color: var(--lumo-contrast-10pct);
  }
  .border-contrast-20 {
    border-color: var(--lumo-contrast-20pct);
  }
  .border-contrast-30 {
    border-color: var(--lumo-contrast-30pct);
  }
  .border-contrast-40 {
    border-color: var(--lumo-contrast-40pct);
  }
  .border-contrast-50 {
    border-color: var(--lumo-contrast-50pct);
  }
  .border-contrast-60 {
    border-color: var(--lumo-contrast-60pct);
  }
  .border-contrast-70 {
    border-color: var(--lumo-contrast-70pct);
  }
  .border-contrast-80 {
    border-color: var(--lumo-contrast-80pct);
  }
  .border-contrast-90 {
    border-color: var(--lumo-contrast-90pct);
  }
  .border-contrast {
    border-color: var(--lumo-contrast);
  }

  .border-primary {
    border-color: var(--lumo-primary-color);
  }
  .border-primary-50 {
    border-color: var(--lumo-primary-color-50pct);
  }
  .border-primary-10 {
    border-color: var(--lumo-primary-color-10pct);
  }

  .border-error {
    border-color: var(--lumo-error-color);
  }
  .border-error-50 {
    border-color: var(--lumo-error-color-50pct);
  }
  .border-error-10 {
    border-color: var(--lumo-error-color-10pct);
  }

  .border-success {
    border-color: var(--lumo-success-color);
  }
  .border-success-50 {
    border-color: var(--lumo-success-color-50pct);
  }
  .border-success-10 {
    border-color: var(--lumo-success-color-10pct);
  }

  /* === Border radius === */
  .rounded-none {
    border-radius: 0;
  }
  .rounded-s {
    border-radius: var(--lumo-border-radius-s);
  }
  .rounded-m {
    border-radius: var(--lumo-border-radius-m);
  }
  .rounded-l {
    border-radius: var(--lumo-border-radius-l);
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const mr=b`
  /* === Align content === */
  .content-center {
    align-content: center;
  }
  .content-end {
    align-content: flex-end;
  }
  .content-start {
    align-content: flex-start;
  }
  .content-around {
    align-content: space-around;
  }
  .content-between {
    align-content: space-between;
  }
  .content-evenly {
    align-content: space-evenly;
  }
  .content-stretch {
    align-content: stretch;
  }

  /* === Align items === */
  .items-baseline {
    align-items: baseline;
  }
  .items-center {
    align-items: center;
  }
  .items-end {
    align-items: flex-end;
  }
  .items-start {
    align-items: flex-start;
  }
  .items-stretch {
    align-items: stretch;
  }

  /* === Align self === */
  .self-auto {
    align-self: auto;
  }
  .self-baseline {
    align-self: baseline;
  }
  .self-center {
    align-self: center;
  }
  .self-end {
    align-self: flex-end;
  }
  .self-start {
    align-self: flex-start;
  }
  .self-stretch {
    align-self: stretch;
  }

  /* === Flex === */
  .flex-auto {
    flex: auto;
  }
  .flex-none {
    flex: none;
  }

  /* === Flex direction === */
  .flex-col {
    flex-direction: column;
  }
  .flex-col-reverse {
    flex-direction: column-reverse;
  }
  .flex-row {
    flex-direction: row;
  }
  .flex-row-reverse {
    flex-direction: row-reverse;
  }

  /* === Flex grow === */
  .flex-grow-0 {
    flex-grow: 0;
  }
  .flex-grow {
    flex-grow: 1;
  }

  /* === Flex shrink === */
  .flex-shrink-0 {
    flex-shrink: 0;
  }
  .flex-shrink {
    flex-shrink: 1;
  }

  /* === Flex wrap === */
  .flex-nowrap {
    flex-wrap: nowrap;
  }
  .flex-wrap {
    flex-wrap: wrap;
  }
  .flex-wrap-reverse {
    flex-wrap: wrap-reverse;
  }

  /* === Gap === */
  .gap-xs {
    gap: var(--lumo-space-xs);
  }
  .gap-s {
    gap: var(--lumo-space-s);
  }
  .gap-m {
    gap: var(--lumo-space-m);
  }
  .gap-l {
    gap: var(--lumo-space-l);
  }
  .gap-xl {
    gap: var(--lumo-space-xl);
  }

  /* === Gap (column) === */
  .gap-x-xs {
    column-gap: var(--lumo-space-xs);
  }
  .gap-x-s {
    column-gap: var(--lumo-space-s);
  }
  .gap-x-m {
    column-gap: var(--lumo-space-m);
  }
  .gap-x-l {
    column-gap: var(--lumo-space-l);
  }
  .gap-x-xl {
    column-gap: var(--lumo-space-xl);
  }

  /* === Gap (row) === */
  .gap-y-xs {
    row-gap: var(--lumo-space-xs);
  }
  .gap-y-s {
    row-gap: var(--lumo-space-s);
  }
  .gap-y-m {
    row-gap: var(--lumo-space-m);
  }
  .gap-y-l {
    row-gap: var(--lumo-space-l);
  }
  .gap-y-xl {
    row-gap: var(--lumo-space-xl);
  }

  /* === Grid auto flow === */
  .grid-flow-col {
    grid-auto-flow: column;
  }
  .grid-flow-row {
    grid-auto-flow: row;
  }

  /* === Grid columns === */
  .grid-cols-1 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
  .grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .grid-cols-3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .grid-cols-4 {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
  .grid-cols-5 {
    grid-template-columns: repeat(5, minmax(0, 1fr));
  }
  .grid-cols-6 {
    grid-template-columns: repeat(6, minmax(0, 1fr));
  }
  .grid-cols-7 {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }
  .grid-cols-8 {
    grid-template-columns: repeat(8, minmax(0, 1fr));
  }
  .grid-cols-9 {
    grid-template-columns: repeat(9, minmax(0, 1fr));
  }
  .grid-cols-10 {
    grid-template-columns: repeat(10, minmax(0, 1fr));
  }
  .grid-cols-11 {
    grid-template-columns: repeat(11, minmax(0, 1fr));
  }
  .grid-cols-12 {
    grid-template-columns: repeat(12, minmax(0, 1fr));
  }

  /* === Grid rows === */
  .grid-rows-1 {
    grid-template-rows: repeat(1, minmax(0, 1fr));
  }
  .grid-rows-2 {
    grid-template-rows: repeat(2, minmax(0, 1fr));
  }
  .grid-rows-3 {
    grid-template-rows: repeat(3, minmax(0, 1fr));
  }
  .grid-rows-4 {
    grid-template-rows: repeat(4, minmax(0, 1fr));
  }
  .grid-rows-5 {
    grid-template-rows: repeat(5, minmax(0, 1fr));
  }
  .grid-rows-6 {
    grid-template-rows: repeat(6, minmax(0, 1fr));
  }

  /* === Justify content === */
  .justify-center {
    justify-content: center;
  }
  .justify-end {
    justify-content: flex-end;
  }
  .justify-start {
    justify-content: flex-start;
  }
  .justify-around {
    justify-content: space-around;
  }
  .justify-between {
    justify-content: space-between;
  }
  .justify-evenly {
    justify-content: space-evenly;
  }

  /* === Span (column) === */
  .col-span-1 {
    grid-column: span 1 / span 1;
  }
  .col-span-2 {
    grid-column: span 2 / span 2;
  }
  .col-span-3 {
    grid-column: span 3 / span 3;
  }
  .col-span-4 {
    grid-column: span 4 / span 4;
  }
  .col-span-5 {
    grid-column: span 5 / span 5;
  }
  .col-span-6 {
    grid-column: span 6 / span 6;
  }
  .col-span-7 {
    grid-column: span 7 / span 7;
  }
  .col-span-8 {
    grid-column: span 8 / span 8;
  }
  .col-span-9 {
    grid-column: span 9 / span 9;
  }
  .col-span-10 {
    grid-column: span 10 / span 10;
  }
  .col-span-11 {
    grid-column: span 11 / span 11;
  }
  .col-span-12 {
    grid-column: span 12 / span 12;
  }

  /* === Span (row) === */
  .row-span-1 {
    grid-row: span 1 / span 1;
  }
  .row-span-2 {
    grid-row: span 2 / span 2;
  }
  .row-span-3 {
    grid-row: span 3 / span 3;
  }
  .row-span-4 {
    grid-row: span 4 / span 4;
  }
  .row-span-5 {
    grid-row: span 5 / span 5;
  }
  .row-span-6 {
    grid-row: span 6 / span 6;
  }

  /* === Responsive design === */
  @media (min-width: 640px) {
    .sm\\:flex-col {
      flex-direction: column;
    }
    .sm\\:flex-row {
      flex-direction: row;
    }
    .sm\\:grid-cols-1 {
      grid-template-columns: repeat(1, minmax(0, 1fr));
    }
    .sm\\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .sm\\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    .sm\\:grid-cols-4 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    .sm\\:grid-cols-5 {
      grid-template-columns: repeat(5, minmax(0, 1fr));
    }
    .sm\\:grid-cols-6 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    .sm\\:grid-cols-7 {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
    .sm\\:grid-cols-8 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    .sm\\:grid-cols-9 {
      grid-template-columns: repeat(9, minmax(0, 1fr));
    }
    .sm\\:grid-cols-10 {
      grid-template-columns: repeat(10, minmax(0, 1fr));
    }
    .sm\\:grid-cols-11 {
      grid-template-columns: repeat(11, minmax(0, 1fr));
    }
    .sm\\:grid-cols-12 {
      grid-template-columns: repeat(12, minmax(0, 1fr));
    }
  }

  @media (min-width: 768px) {
    .md\\:flex-col {
      flex-direction: column;
    }
    .md\\:flex-row {
      flex-direction: row;
    }
    .md\\:grid-cols-1 {
      grid-template-columns: repeat(1, minmax(0, 1fr));
    }
    .md\\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .md\\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    .md\\:grid-cols-4 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    .md\\:grid-cols-5 {
      grid-template-columns: repeat(5, minmax(0, 1fr));
    }
    .md\\:grid-cols-6 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    .md\\:grid-cols-7 {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
    .md\\:grid-cols-8 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    .md\\:grid-cols-9 {
      grid-template-columns: repeat(9, minmax(0, 1fr));
    }
    .md\\:grid-cols-10 {
      grid-template-columns: repeat(10, minmax(0, 1fr));
    }
    .md\\:grid-cols-11 {
      grid-template-columns: repeat(11, minmax(0, 1fr));
    }
    .md\\:grid-cols-12 {
      grid-template-columns: repeat(12, minmax(0, 1fr));
    }
  }
  @media (min-width: 1024px) {
    .lg\\:flex-col {
      flex-direction: column;
    }
    .lg\\:flex-row {
      flex-direction: row;
    }
    .lg\\:grid-cols-1 {
      grid-template-columns: repeat(1, minmax(0, 1fr));
    }
    .lg\\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .lg\\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    .lg\\:grid-cols-4 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    .lg\\:grid-cols-5 {
      grid-template-columns: repeat(5, minmax(0, 1fr));
    }
    .lg\\:grid-cols-6 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    .lg\\:grid-cols-7 {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
    .lg\\:grid-cols-8 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    .lg\\:grid-cols-9 {
      grid-template-columns: repeat(9, minmax(0, 1fr));
    }
    .lg\\:grid-cols-10 {
      grid-template-columns: repeat(10, minmax(0, 1fr));
    }
    .lg\\:grid-cols-11 {
      grid-template-columns: repeat(11, minmax(0, 1fr));
    }
    .lg\\:grid-cols-12 {
      grid-template-columns: repeat(12, minmax(0, 1fr));
    }
  }
  @media (min-width: 1280px) {
    .xl\\:flex-col {
      flex-direction: column;
    }
    .xl\\:flex-row {
      flex-direction: row;
    }
    .xl\\:grid-cols-1 {
      grid-template-columns: repeat(1, minmax(0, 1fr));
    }
    .xl\\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .xl\\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    .xl\\:grid-cols-4 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    .xl\\:grid-cols-5 {
      grid-template-columns: repeat(5, minmax(0, 1fr));
    }
    .xl\\:grid-cols-6 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    .xl\\:grid-cols-7 {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
    .xl\\:grid-cols-8 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    .xl\\:grid-cols-9 {
      grid-template-columns: repeat(9, minmax(0, 1fr));
    }
    .xl\\:grid-cols-10 {
      grid-template-columns: repeat(10, minmax(0, 1fr));
    }
    .xl\\:grid-cols-11 {
      grid-template-columns: repeat(11, minmax(0, 1fr));
    }
    .xl\\:grid-cols-12 {
      grid-template-columns: repeat(12, minmax(0, 1fr));
    }
  }
  @media (min-width: 1536px) {
    .\\32xl\\:flex-col {
      flex-direction: column;
    }
    .\\32xl\\:flex-row {
      flex-direction: row;
    }
    .\\32xl\\:grid-cols-1 {
      grid-template-columns: repeat(1, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-4 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-5 {
      grid-template-columns: repeat(5, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-6 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-7 {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-8 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-9 {
      grid-template-columns: repeat(9, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-10 {
      grid-template-columns: repeat(10, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-11 {
      grid-template-columns: repeat(11, minmax(0, 1fr));
    }
    .\\32xl\\:grid-cols-12 {
      grid-template-columns: repeat(12, minmax(0, 1fr));
    }
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const hr=b`
  /* === Box sizing === */
  .box-border {
    box-sizing: border-box;
  }
  .box-content {
    box-sizing: content-box;
  }

  /* === Display === */
  .block {
    display: block;
  }
  .flex {
    display: flex;
  }
  .hidden {
    display: none;
  }
  .inline {
    display: inline;
  }
  .inline-block {
    display: inline-block;
  }
  .inline-flex {
    display: inline-flex;
  }
  .inline-grid {
    display: inline-grid;
  }
  .grid {
    display: grid;
  }

  /* === Overflow === */
  .overflow-auto {
    overflow: auto;
  }
  .overflow-hidden {
    overflow: hidden;
  }
  .overflow-scroll {
    overflow: scroll;
  }

  /* === Position === */
  .absolute {
    position: absolute;
  }
  .fixed {
    position: fixed;
  }
  .static {
    position: static;
  }
  .sticky {
    position: sticky;
  }
  .relative {
    position: relative;
  }

  /* === Responsive design === */
  @media (min-width: 640px) {
    .sm\\:flex {
      display: flex;
    }
    .sm\\:hidden {
      display: none;
    }
  }
  @media (min-width: 768px) {
    .md\\:flex {
      display: flex;
    }
    .md\\:hidden {
      display: none;
    }
  }
  @media (min-width: 1024px) {
    .lg\\:flex {
      display: flex;
    }
    .lg\\:hidden {
      display: none;
    }
  }
  @media (min-width: 1280px) {
    .xl\\:flex {
      display: flex;
    }
    .xl\\:hidden {
      display: none;
    }
  }
  @media (min-width: 1536px) {
    .\\32xl\\:flex {
      display: flex;
    }
    .\\32xl\\:hidden {
      display: none;
    }
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const pr=b`
  /* === Box shadows === */
  .shadow-xs {
    box-shadow: var(--lumo-box-shadow-xs);
  }
  .shadow-s {
    box-shadow: var(--lumo-box-shadow-s);
  }
  .shadow-m {
    box-shadow: var(--lumo-box-shadow-m);
  }
  .shadow-l {
    box-shadow: var(--lumo-box-shadow-l);
  }
  .shadow-xl {
    box-shadow: var(--lumo-box-shadow-xl);
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const fr=b`
  /* === Height === */
  .h-0 {
    height: 0;
  }
  .h-xs {
    height: var(--lumo-size-xs);
  }
  .h-s {
    height: var(--lumo-size-s);
  }
  .h-m {
    height: var(--lumo-size-m);
  }
  .h-l {
    height: var(--lumo-size-l);
  }
  .h-xl {
    height: var(--lumo-size-xl);
  }
  .h-auto {
    height: auto;
  }
  .h-full {
    height: 100%;
  }
  .h-screen {
    height: 100vh;
  }

  /* === Height (max) === */
  .max-h-full {
    max-height: 100%;
  }
  .max-h-screen {
    max-height: 100vh;
  }

  /* === Height (min) === */
  .min-h-0 {
    min-height: 0;
  }
  .min-h-full {
    min-height: 100%;
  }
  .min-h-screen {
    min-height: 100vh;
  }

  /* === Icon sizing === */
  .icon-s {
    height: var(--lumo-icon-size-s);
    width: var(--lumo-icon-size-s);
  }
  .icon-m {
    height: var(--lumo-icon-size-m);
    width: var(--lumo-icon-size-m);
  }
  .icon-l {
    height: var(--lumo-icon-size-l);
    width: var(--lumo-icon-size-l);
  }

  /* === Width === */
  .w-xs {
    width: var(--lumo-size-xs);
  }
  .w-s {
    width: var(--lumo-size-s);
  }
  .w-m {
    width: var(--lumo-size-m);
  }
  .w-l {
    width: var(--lumo-size-l);
  }
  .w-xl {
    width: var(--lumo-size-xl);
  }
  .w-auto {
    width: auto;
  }
  .w-full {
    width: 100%;
  }

  /* === Width (max) === */
  .max-w-full {
    max-width: 100%;
  }
  .max-w-screen-sm {
    max-width: 640px;
  }
  .max-w-screen-md {
    max-width: 768px;
  }
  .max-w-screen-lg {
    max-width: 1024px;
  }
  .max-w-screen-xl {
    max-width: 1280px;
  }
  .max-w-screen-2xl {
    max-width: 1536px;
  }

  /* === Width (min) === */
  .min-w-0 {
    min-width: 0;
  }
  .min-w-full {
    min-width: 100%;
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const gr=b`
  /* === Margin === */
  .m-auto {
    margin: auto;
  }
  .m-0 {
    margin: 0;
  }
  .m-xs {
    margin: var(--lumo-space-xs);
  }
  .m-s {
    margin: var(--lumo-space-s);
  }
  .m-m {
    margin: var(--lumo-space-m);
  }
  .m-l {
    margin: var(--lumo-space-l);
  }
  .m-xl {
    margin: var(--lumo-space-xl);
  }

  /* === Margin (bottom) === */
  .mb-auto {
    margin-bottom: auto;
  }
  .mb-0 {
    margin-bottom: 0;
  }
  .mb-xs {
    margin-bottom: var(--lumo-space-xs);
  }
  .mb-s {
    margin-bottom: var(--lumo-space-s);
  }
  .mb-m {
    margin-bottom: var(--lumo-space-m);
  }
  .mb-l {
    margin-bottom: var(--lumo-space-l);
  }
  .mb-xl {
    margin-bottom: var(--lumo-space-xl);
  }

  /* === Margin (end) === */
  .me-auto {
    margin-inline-end: auto;
  }
  .me-0 {
    margin-inline-end: 0;
  }
  .me-xs {
    margin-inline-end: var(--lumo-space-xs);
  }
  .me-s {
    margin-inline-end: var(--lumo-space-s);
  }
  .me-m {
    margin-inline-end: var(--lumo-space-m);
  }
  .me-l {
    margin-inline-end: var(--lumo-space-l);
  }
  .me-xl {
    margin-inline-end: var(--lumo-space-xl);
  }

  /* === Margin (horizontal) === */
  .mx-auto {
    margin-left: auto;
    margin-right: auto;
  }
  .mx-0 {
    margin-left: 0;
    margin-right: 0;
  }
  .mx-xs {
    margin-left: var(--lumo-space-xs);
    margin-right: var(--lumo-space-xs);
  }
  .mx-s {
    margin-left: var(--lumo-space-s);
    margin-right: var(--lumo-space-s);
  }
  .mx-m {
    margin-left: var(--lumo-space-m);
    margin-right: var(--lumo-space-m);
  }
  .mx-l {
    margin-left: var(--lumo-space-l);
    margin-right: var(--lumo-space-l);
  }
  .mx-xl {
    margin-left: var(--lumo-space-xl);
    margin-right: var(--lumo-space-xl);
  }

  /* === Margin (left) === */
  .ml-auto {
    margin-left: auto;
  }
  .ml-0 {
    margin-left: 0;
  }
  .ml-xs {
    margin-left: var(--lumo-space-xs);
  }
  .ml-s {
    margin-left: var(--lumo-space-s);
  }
  .ml-m {
    margin-left: var(--lumo-space-m);
  }
  .ml-l {
    margin-left: var(--lumo-space-l);
  }
  .ml-xl {
    margin-left: var(--lumo-space-xl);
  }

  /* === Margin (right) === */
  .mr-auto {
    margin-right: auto;
  }
  .mr-0 {
    margin-right: 0;
  }
  .mr-xs {
    margin-right: var(--lumo-space-xs);
  }
  .mr-s {
    margin-right: var(--lumo-space-s);
  }
  .mr-m {
    margin-right: var(--lumo-space-m);
  }
  .mr-l {
    margin-right: var(--lumo-space-l);
  }
  .mr-xl {
    margin-right: var(--lumo-space-xl);
  }

  /* === Margin (start) === */
  .ms-auto {
    margin-inline-start: auto;
  }
  .ms-0 {
    margin-inline-start: 0;
  }
  .ms-xs {
    margin-inline-start: var(--lumo-space-xs);
  }
  .ms-s {
    margin-inline-start: var(--lumo-space-s);
  }
  .ms-m {
    margin-inline-start: var(--lumo-space-m);
  }
  .ms-l {
    margin-inline-start: var(--lumo-space-l);
  }
  .ms-xl {
    margin-inline-start: var(--lumo-space-xl);
  }

  /* === Margin (top) === */
  .mt-auto {
    margin-top: auto;
  }
  .mt-0 {
    margin-top: 0;
  }
  .mt-xs {
    margin-top: var(--lumo-space-xs);
  }
  .mt-s {
    margin-top: var(--lumo-space-s);
  }
  .mt-m {
    margin-top: var(--lumo-space-m);
  }
  .mt-l {
    margin-top: var(--lumo-space-l);
  }
  .mt-xl {
    margin-top: var(--lumo-space-xl);
  }

  /* === Margin (vertical) === */
  .my-auto {
    margin-bottom: auto;
    margin-top: auto;
  }
  .my-0 {
    margin-bottom: 0;
    margin-top: 0;
  }
  .my-xs {
    margin-bottom: var(--lumo-space-xs);
    margin-top: var(--lumo-space-xs);
  }
  .my-s {
    margin-bottom: var(--lumo-space-s);
    margin-top: var(--lumo-space-s);
  }
  .my-m {
    margin-bottom: var(--lumo-space-m);
    margin-top: var(--lumo-space-m);
  }
  .my-l {
    margin-bottom: var(--lumo-space-l);
    margin-top: var(--lumo-space-l);
  }
  .my-xl {
    margin-bottom: var(--lumo-space-xl);
    margin-top: var(--lumo-space-xl);
  }

  /* === Padding === */
  .p-0 {
    padding: 0;
  }
  .p-xs {
    padding: var(--lumo-space-xs);
  }
  .p-s {
    padding: var(--lumo-space-s);
  }
  .p-m {
    padding: var(--lumo-space-m);
  }
  .p-l {
    padding: var(--lumo-space-l);
  }
  .p-xl {
    padding: var(--lumo-space-xl);
  }

  /* === Padding (bottom) === */
  .pb-0 {
    padding-bottom: 0;
  }
  .pb-xs {
    padding-bottom: var(--lumo-space-xs);
  }
  .pb-s {
    padding-bottom: var(--lumo-space-s);
  }
  .pb-m {
    padding-bottom: var(--lumo-space-m);
  }
  .pb-l {
    padding-bottom: var(--lumo-space-l);
  }
  .pb-xl {
    padding-bottom: var(--lumo-space-xl);
  }

  /* === Padding (end) === */
  .pe-0 {
    padding-inline-end: 0;
  }
  .pe-xs {
    padding-inline-end: var(--lumo-space-xs);
  }
  .pe-s {
    padding-inline-end: var(--lumo-space-s);
  }
  .pe-m {
    padding-inline-end: var(--lumo-space-m);
  }
  .pe-l {
    padding-inline-end: var(--lumo-space-l);
  }
  .pe-xl {
    padding-inline-end: var(--lumo-space-xl);
  }

  /* === Padding (horizontal) === */
  .px-0 {
    padding-left: 0;
    padding-right: 0;
  }
  .px-xs {
    padding-left: var(--lumo-space-xs);
    padding-right: var(--lumo-space-xs);
  }
  .px-s {
    padding-left: var(--lumo-space-s);
    padding-right: var(--lumo-space-s);
  }
  .px-m {
    padding-left: var(--lumo-space-m);
    padding-right: var(--lumo-space-m);
  }
  .px-l {
    padding-left: var(--lumo-space-l);
    padding-right: var(--lumo-space-l);
  }
  .px-xl {
    padding-left: var(--lumo-space-xl);
    padding-right: var(--lumo-space-xl);
  }

  /* === Padding (left) === */
  .pl-0 {
    padding-left: 0;
  }
  .pl-xs {
    padding-left: var(--lumo-space-xs);
  }
  .pl-s {
    padding-left: var(--lumo-space-s);
  }
  .pl-m {
    padding-left: var(--lumo-space-m);
  }
  .pl-l {
    padding-left: var(--lumo-space-l);
  }
  .pl-xl {
    padding-left: var(--lumo-space-xl);
  }

  /* === Padding (right) === */
  .pr-0 {
    padding-right: 0;
  }
  .pr-xs {
    padding-right: var(--lumo-space-xs);
  }
  .pr-s {
    padding-right: var(--lumo-space-s);
  }
  .pr-m {
    padding-right: var(--lumo-space-m);
  }
  .pr-l {
    padding-right: var(--lumo-space-l);
  }
  .pr-xl {
    padding-right: var(--lumo-space-xl);
  }

  /* === Padding (start) === */
  .ps-0 {
    padding-inline-start: 0;
  }
  .ps-xs {
    padding-inline-start: var(--lumo-space-xs);
  }
  .ps-s {
    padding-inline-start: var(--lumo-space-s);
  }
  .ps-m {
    padding-inline-start: var(--lumo-space-m);
  }
  .ps-l {
    padding-inline-start: var(--lumo-space-l);
  }
  .ps-xl {
    padding-inline-start: var(--lumo-space-xl);
  }

  /* === Padding (top) === */
  .pt-0 {
    padding-top: 0;
  }
  .pt-xs {
    padding-top: var(--lumo-space-xs);
  }
  .pt-s {
    padding-top: var(--lumo-space-s);
  }
  .pt-m {
    padding-top: var(--lumo-space-m);
  }
  .pt-l {
    padding-top: var(--lumo-space-l);
  }
  .pt-xl {
    padding-top: var(--lumo-space-xl);
  }

  /* === Padding (vertical) === */
  .py-0 {
    padding-bottom: 0;
    padding-top: 0;
  }
  .py-xs {
    padding-bottom: var(--lumo-space-xs);
    padding-top: var(--lumo-space-xs);
  }
  .py-s {
    padding-bottom: var(--lumo-space-s);
    padding-top: var(--lumo-space-s);
  }
  .py-m {
    padding-bottom: var(--lumo-space-m);
    padding-top: var(--lumo-space-m);
  }
  .py-l {
    padding-bottom: var(--lumo-space-l);
    padding-top: var(--lumo-space-l);
  }
  .py-xl {
    padding-bottom: var(--lumo-space-xl);
    padding-top: var(--lumo-space-xl);
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const vr=b`
  /* === Font size === */
  .text-2xs {
    font-size: var(--lumo-font-size-xxs);
  }
  .text-xs {
    font-size: var(--lumo-font-size-xs);
  }
  .text-s {
    font-size: var(--lumo-font-size-s);
  }
  .text-m {
    font-size: var(--lumo-font-size-m);
  }
  .text-l {
    font-size: var(--lumo-font-size-l);
  }
  .text-xl {
    font-size: var(--lumo-font-size-xl);
  }
  .text-2xl {
    font-size: var(--lumo-font-size-xxl);
  }
  .text-3xl {
    font-size: var(--lumo-font-size-xxxl);
  }

  /* === Font weight === */
  .font-thin {
    font-weight: 100;
  }
  .font-extralight {
    font-weight: 200;
  }
  .font-light {
    font-weight: 300;
  }
  .font-normal {
    font-weight: 400;
  }
  .font-medium {
    font-weight: 500;
  }
  .font-semibold {
    font-weight: 600;
  }
  .font-bold {
    font-weight: 700;
  }
  .font-extrabold {
    font-weight: 800;
  }
  .font-black {
    font-weight: 900;
  }

  /* === Line height === */
  .leading-none {
    line-height: 1;
  }
  .leading-xs {
    line-height: var(--lumo-line-height-xs);
  }
  .leading-s {
    line-height: var(--lumo-line-height-s);
  }
  .leading-m {
    line-height: var(--lumo-line-height-m);
  }

  /* === List style type === */
  .list-none {
    list-style-type: none;
  }

  /* === Text alignment === */
  .text-left {
    text-align: left;
  }
  .text-center {
    text-align: center;
  }
  .text-right {
    text-align: right;
  }
  .text-justify {
    text-align: justify;
  }

  /* === Text color === */
  .text-header {
    color: var(--lumo-header-text-color);
  }
  .text-body {
    color: var(--lumo-body-text-color);
  }
  .text-secondary {
    color: var(--lumo-secondary-text-color);
  }
  .text-tertiary {
    color: var(--lumo-tertiary-text-color);
  }
  .text-disabled {
    color: var(--lumo-disabled-text-color);
  }
  .text-primary {
    color: var(--lumo-primary-text-color);
  }
  .text-primary-contrast {
    color: var(--lumo-primary-contrast-color);
  }
  .text-error {
    color: var(--lumo-error-text-color);
  }
  .text-error-contrast {
    color: var(--lumo-error-contrast-color);
  }
  .text-success {
    color: var(--lumo-success-text-color);
  }
  .text-success-contrast {
    color: var(--lumo-success-contrast-color);
  }

  /* === Text overflow === */
  .overflow-clip {
    text-overflow: clip;
  }
  .overflow-ellipsis {
    text-overflow: ellipsis;
  }

  /* === Text transform === */
  .capitalize {
    text-transform: capitalize;
  }
  .lowercase {
    text-transform: lowercase;
  }
  .uppercase {
    text-transform: uppercase;
  }

  /* === Whitespace === */
  .whitespace-normal {
    white-space: normal;
  }
  .whitespace-nowrap {
    white-space: nowrap;
  }
  .whitespace-pre {
    white-space: pre;
  }
  .whitespace-pre-line {
    white-space: pre-line;
  }
  .whitespace-pre-wrap {
    white-space: pre-wrap;
  }

  /* === Responsive design === */
  @media (min-width: 640px) {
    .sm\\:text-2xs {
      font-size: var(--lumo-font-size-xxs);
    }
    .sm\\:text-xs {
      font-size: var(--lumo-font-size-xs);
    }
    .sm\\:text-s {
      font-size: var(--lumo-font-size-s);
    }
    .sm\\:text-m {
      font-size: var(--lumo-font-size-m);
    }
    .sm\\:text-l {
      font-size: var(--lumo-font-size-l);
    }
    .sm\\:text-xl {
      font-size: var(--lumo-font-size-xl);
    }
    .sm\\:text-2xl {
      font-size: var(--lumo-font-size-xxl);
    }
    .sm\\:text-3xl {
      font-size: var(--lumo-font-size-xxxl);
    }
  }
  @media (min-width: 768px) {
    .md\\:text-2xs {
      font-size: var(--lumo-font-size-xxs);
    }
    .md\\:text-xs {
      font-size: var(--lumo-font-size-xs);
    }
    .md\\:text-s {
      font-size: var(--lumo-font-size-s);
    }
    .md\\:text-m {
      font-size: var(--lumo-font-size-m);
    }
    .md\\:text-l {
      font-size: var(--lumo-font-size-l);
    }
    .md\\:text-xl {
      font-size: var(--lumo-font-size-xl);
    }
    .md\\:text-2xl {
      font-size: var(--lumo-font-size-xxl);
    }
    .md\\:text-3xl {
      font-size: var(--lumo-font-size-xxxl);
    }
  }
  @media (min-width: 1024px) {
    .lg\\:text-2xs {
      font-size: var(--lumo-font-size-xxs);
    }
    .lg\\:text-xs {
      font-size: var(--lumo-font-size-xs);
    }
    .lg\\:text-s {
      font-size: var(--lumo-font-size-s);
    }
    .lg\\:text-m {
      font-size: var(--lumo-font-size-m);
    }
    .lg\\:text-l {
      font-size: var(--lumo-font-size-l);
    }
    .lg\\:text-xl {
      font-size: var(--lumo-font-size-xl);
    }
    .lg\\:text-2xl {
      font-size: var(--lumo-font-size-xxl);
    }
    .lg\\:text-3xl {
      font-size: var(--lumo-font-size-xxxl);
    }
  }
  @media (min-width: 1280px) {
    .xl\\:text-2xs {
      font-size: var(--lumo-font-size-xxs);
    }
    .xl\\:text-xs {
      font-size: var(--lumo-font-size-xs);
    }
    .xl\\:text-s {
      font-size: var(--lumo-font-size-s);
    }
    .xl\\:text-m {
      font-size: var(--lumo-font-size-m);
    }
    .xl\\:text-l {
      font-size: var(--lumo-font-size-l);
    }
    .xl\\:text-xl {
      font-size: var(--lumo-font-size-xl);
    }
    .xl\\:text-2xl {
      font-size: var(--lumo-font-size-xxl);
    }
    .xl\\:text-3xl {
      font-size: var(--lumo-font-size-xxxl);
    }
  }
  @media (min-width: 1536px) {
    .\\32xl\\:text-2xs {
      font-size: var(--lumo-font-size-xxs);
    }
    .\\32xl\\:text-xs {
      font-size: var(--lumo-font-size-xs);
    }
    .\\32xl\\:text-s {
      font-size: var(--lumo-font-size-s);
    }
    .\\32xl\\:text-m {
      font-size: var(--lumo-font-size-m);
    }
    .\\32xl\\:text-l {
      font-size: var(--lumo-font-size-l);
    }
    .\\32xl\\:text-xl {
      font-size: var(--lumo-font-size-xl);
    }
    .\\32xl\\:text-2xl {
      font-size: var(--lumo-font-size-xxl);
    }
    .\\32xl\\:text-3xl {
      font-size: var(--lumo-font-size-xxxl);
    }
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ci=b`
${cr}
${dr}
${ur}
${pr}
${mr}
${hr}
${fr}
${gr}
${vr}
`;ke("",ci,{moduleId:"lumo-utility"});const yr=b`:host(:not([dir="rtl"]):not([overlay])) [part=drawer]{border-right:0 solid var(--lumo-contrast-10pct)}
`,ye=(o,e)=>{const t=document.createElement("style");t.type="text/css",t.appendChild(document.createTextNode(o)),e===document?document.head.appendChild(t):e.appendChild(t)};window.Vaadin=window.Vaadin||{};window.Vaadin.theme=window.Vaadin.theme||{};window.Vaadin.theme.injectedGlobalCss=[];const br=o=>{document._vaadintheme_jtaf_componentCss||(ke("vaadin-app-layout",Vo(yr.toString())),document._vaadintheme_jtaf_componentCss=!0),ye(ti.cssText,o),ye(ni.cssText,o),ye(ri.cssText,o),ye(li.cssText,o),ye(ci.cssText,o)},xr=br;xr(document);export{zn as D,te as L,Pn as P,Er as T,Qn as a,Fe as b,b as c,Z as d,On as e,ni as f,Qo as g,R as h,_ as i,fe as j,S as n,Xn as p,In as q,ke as r,_r as s,ti as t,Vo as u};
