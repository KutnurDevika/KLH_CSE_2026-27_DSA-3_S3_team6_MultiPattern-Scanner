import React,{useState}from"react";
import{createRoot}from"react-dom/client";
import*as pdfjsLib from"pdfjs-dist";
import"./styles.css";

pdfjsLib.GlobalWorkerOptions.workerSrc=new URL("pdfjs-dist/build/pdf.worker.min.mjs",import.meta.url).toString();

class Node{constructor(){this.next=new Map();this.fail=null;this.out=[]}}
function build(patterns){
 const root=new Node();root.fail=root;let nodes=1;
 patterns.forEach((p,id)=>{let n=root;for(const c of Array.from(p)){if(!n.next.has(c)){n.next.set(c,new Node());nodes++}n=n.next.get(c)}n.out.push(id)});
 const q=[];for(const n of root.next.values()){n.fail=root;q.push(n)}
 while(q.length){const n=q.shift();for(const[c,ch]of n.next){let f=n.fail;while(f!==root&&!f.next.has(c))f=f.fail;ch.fail=f.next.has(c)&&f.next.get(c)!==ch?f.next.get(c):root;ch.out=[...ch.out,...ch.fail.out];q.push(ch)}}
 return{root,nodes}
}
function scan(text,rawPatterns){
 const patterns=[...new Set(rawPatterns.map(x=>x.trim()).filter(Boolean))];
 if(!text||!patterns.length)return{patterns,hits:[],nodes:0,build:0,search:0};
 const t=performance.now(),a=build(patterns),buildTime=performance.now()-t;
 const chars=Array.from(text),offsets=[];let off=0;
 for(const c of chars){offsets.push(off);off+=c.length}
 const hits=patterns.map(()=>[]);let state=a.root;
 const s=performance.now();
 chars.forEach((c,i)=>{
   while(state!==a.root&&!state.next.has(c))state=state.fail;
   state=state.next.has(c)?state.next.get(c):a.root;
   for(const id of state.out){
     const len=Array.from(patterns[id]).length;
     hits[id].push(offsets[Math.max(0,i-len+1)]);
   }
 });
 return{patterns,hits,nodes:a.nodes,build:buildTime,search:performance.now()-s};
}
async function getPdfText(file){
 const pdf=await pdfjsLib.getDocument({data:await file.arrayBuffer()}).promise;
 const pages=[];
 for(let i=1;i<=pdf.numPages;i++){
   const page=await pdf.getPage(i),c=await page.getTextContent();
   pages.push(c.items.map(x=>x.str).join(" "));
 }
 return pages.join("\n\n");
}

function App(){
 const[text,setText]=useState(""),[patterns,setPatterns]=useState(""),[fileName,setFileName]=useState(""),[result,setResult]=useState(null),[busy,setBusy]=useState(false);
 const patternList=patterns.split("\n").map(x=>x.trim()).filter(Boolean);
 const total=result?result.hits.reduce((a,x)=>a+x.length,0):0;
 async function file(e){
   const f=e.target.files?.[0];if(!f)return;
   setBusy(true);setFileName(f.name);setResult(null);
   try{
     if(f.name.toLowerCase().endsWith(".pdf"))setText(await getPdfText(f));
     else setText(await f.text());
   }catch{alert("Could not read this file. Please use a TXT file or a text-based PDF.")}
   finally{setBusy(false);e.target.value=""}
 }
 function run(){setResult(scan(text,patternList))}
 return <div className="page">
   <header><div className="logo">M</div><div><h1>MultiPattern Scanner</h1><p>Aho–Corasick Multi-Pattern Search Engine</p></div></header>

   <main>
    <section className="input-card">
      <div className="card-title"><span>INPUT</span><label className="upload">Upload TXT / PDF<input type="file" accept=".txt,.pdf" onChange={file}/></label></div>
      <div className="file">{fileName||"No file selected"}{busy&&" — reading..."}</div>
      <textarea className="text" value={text} onChange={e=>setText(e.target.value)} placeholder="Paste or type your text here..."/>
    </section>

    <section className="input-card">
      <div className="card-title"><span>PATTERNS</span><small>One pattern per line</small></div>
      <textarea className="patterns" value={patterns} onChange={e=>setPatterns(e.target.value)} placeholder={"ERROR\nWARNING\ntimeout\nUnauthorized"}/>
      <button className="scan" disabled={!text||!patternList.length||busy} onClick={run}>SCAN WITH AHO–CORASICK</button>
    </section>

    <section className="output-card">
      <div className="output-head"><span>OUTPUT</span>{result&&<small>{total} total occurrence(s)</small>}</div>
      {!result?<div className="empty">Enter text, add patterns, and click Scan.</div>:
      <div className="output">
        <div className="line">============================================================</div>
        <h2>MULTIPATTERN SCANNER</h2>
        <div className="line">============================================================</div>
        <p><b>[INFO]</b> Characters processed : {text.length}</p>
        <p><b>[INFO]</b> Patterns searched   : {result.patterns.length}</p>
        <p><b>[OK]</b> Trie constructed      : {result.nodes} nodes</p>
        <p><b>[OK]</b> Failure links         : Generated using BFS</p>
        <p><b>[INFO]</b> Search started...</p>
        <div className="line">------------------------------------------------------------</div>
        {result.patterns.map((p,i)=><div className={result.hits[i].length?"match":"nomatch"} key={p}>
          {result.hits[i].length
            ? <><b>[MATCH]</b> {p} → {result.hits[i].length} occurrence(s)<div className="positions">Positions: {result.hits[i].join(", ")}</div></>
            : <><b>[NO MATCH]</b> {p}</>}
        </div>)}
        <div className="line">------------------------------------------------------------</div>
        <h3>SEARCH SUMMARY</h3>
        <p>Patterns searched  : {result.patterns.length}</p>
        <p>Patterns matched   : {result.hits.filter(x=>x.length).length}</p>
        <p>Total occurrences : {total}</p>
        <p>Trie nodes         : {result.nodes}</p>
        <p>Build time         : {result.build.toFixed(3)} ms</p>
        <p>Search time        : {result.search.toFixed(3)} ms</p>
        <p className="success">[OK] Scan completed successfully.</p>
        <div className="line">============================================================</div>
      </div>}
    </section>
   </main>
 </div>
}
createRoot(document.getElementById("root")).render(<App/>);