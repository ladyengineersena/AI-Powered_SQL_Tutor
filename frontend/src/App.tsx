import { useState, useEffect } from 'react'
import axios from 'axios'
import { Search, Play, HelpCircle, Zap, ListChecks, AlertCircle } from 'lucide-react'

interface SqlResponse {
  generatedSql?: string;
  results?: any[];
  explainPlan?: string;
  performanceSuggestions?: string;
  errorExplanation?: string;
  miniTasks?: string[];
}

function App() {
  const [query, setQuery] = useState('')
  const [loading, setLoading] = useState(false)
  const [response, setResponse] = useState<SqlResponse | null>(null)
  const [tasks, setTasks] = useState<string[]>([])

  useEffect(() => {
    fetchTasks()
  }, [])

  const fetchTasks = async () => {
    try {
      const res = await axios.get('http://127.0.0.1:9101/api/tutor/tasks')
      setTasks(res.data)
    } catch (err) {
      console.error("Failed to fetch tasks", err)
      setTasks([
        "1. Tüm kullanıcıları listele",
        "2. En pahalı ürünleri bul",
        "3. Son satışları görüntüle"
      ])
    }
  }

  const handleSubmit = async (e?: React.FormEvent, overrideQuery?: string) => {
    if (e) e.preventDefault()
    const finalQuery = overrideQuery || query
    if (!finalQuery) return

    setLoading(true)
    setResponse(null)
    console.log("Sorgu gönderiliyor:", finalQuery);
    try {
      const res = await axios.post('http://127.0.0.1:9101/api/tutor/query', {
        naturalLanguageQuery: finalQuery
      })
      
      console.log("Backend yanıtı alındı:", res.data);
      setResponse(res.data)
      if (res.data.miniTasks) setTasks(res.data.miniTasks)
    } catch (err: any) {
      console.error("Query failed", err)
      const errorMsg = err.response?.data?.message || err.message || "Bilinmeyen bir hata oluştu.";
      alert(`HATA: Backend ile iletişim kurulamadı.\n\nDetay: ${errorMsg}\n\nLütfen backend penceresindeki kırmızı yazıları kontrol edin.`);
    } finally {
      setLoading(false)
    }
  }

  const handleTaskClick = (task: string) => {
    const cleanQuery = task.replace(/^\d+\.\s*/, '')
    setQuery(cleanQuery)
    handleSubmit(undefined, cleanQuery)
  }

  return (
    <div className="min-h-screen p-8 max-w-6xl mx-auto">
      <header className="mb-8 text-center">
        <h1 className="text-4xl font-bold text-blue-800 mb-2">AI SQL Tutor</h1>
        <p className="text-gray-600 mb-4">Doğal dil ile SQL öğrenin ve veritabanınızı sorgulayın</p>
        
        {response?.generatedSql?.includes("API Anahtarı eksik") && (
          <div className="inline-block bg-amber-100 text-amber-800 px-4 py-2 rounded-full text-sm font-medium border border-amber-200">
            ⚠️ Simülasyon Modu Aktif (API Anahtarı Eksik)
          </div>
        )}
      </header>

      <main className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          {/* SQL Section */}
          <section className="bg-white p-6 rounded-xl shadow-md">
            <h2 className="text-xl font-semibold mb-4 flex items-center">
              <Search className="mr-2 text-blue-500" /> Soru Sor
            </h2>
            <form onSubmit={handleSubmit} className="flex gap-2">
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Örn: Son 30 günde en çok satış yapan kullanıcıları getir."
                className="flex-1 p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
                autoComplete="off"
                disabled={loading}
              />
              <button
                type="submit"
                disabled={loading || !query}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 flex items-center"
              >
                {loading ? 'İşleniyor...' : <><Play size={18} className="mr-1" /> Gönder</>}
              </button>
            </form>
          </section>

          {response && (
            <>
              {/* SQL & Results */}
              <section className="bg-white p-6 rounded-xl shadow-md">
                <h2 className="text-xl font-semibold mb-4 flex items-center">
                  <Zap className="mr-2 text-yellow-500" /> Üretilen SQL
                </h2>
                <pre className="bg-gray-800 text-green-400 p-4 rounded-lg overflow-x-auto mb-6">
                  <code>{response.generatedSql}</code>
                </pre>

                {response.results && response.results.length > 0 && (
                  <>
                    <h3 className="font-semibold mb-2">Sonuçlar:</h3>
                    <div className="overflow-x-auto">
                      <table className="min-w-full border">
                        <thead>
                          <tr className="bg-gray-50">
                            {Object.keys(response.results[0]).map(key => (
                              <th key={key} className="p-2 border text-left text-sm font-medium">{key}</th>
                            ))}
                          </tr>
                        </thead>
                        <tbody>
                          {response.results.map((row, i) => (
                            <tr key={i}>
                              {Object.values(row).map((val: any, j) => (
                                <td key={j} className="p-2 border text-sm">{val?.toString()}</td>
                              ))}
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </>
                )}

                {response.errorExplanation && (
                  <div className="bg-red-50 border-l-4 border-red-500 p-4 mt-4">
                    <h3 className="text-red-700 font-bold flex items-center mb-1">
                      <AlertCircle size={18} className="mr-2" /> Hata Analizi
                    </h3>
                    <p className="text-red-600 text-sm whitespace-pre-wrap">{response.errorExplanation}</p>
                  </div>
                )}

                {response.miniTasks && response.miniTasks.length > 0 && (
                  <div className="mt-6">
                    <h3 className="font-semibold mb-3 flex items-center text-blue-800">
                      <Zap size={18} className="mr-2 text-yellow-500" /> Önerilen Devam Görevleri:
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      {response.miniTasks.map((task, idx) => (
                        <div key={idx} 
                             onClick={() => handleTaskClick(task)}
                             className="p-3 bg-blue-50 text-blue-800 rounded-lg text-sm border border-blue-100 cursor-pointer hover:bg-blue-100 transition-colors">
                          {task}
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </section>

              {/* Explain Plan & Suggestions */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <section className="bg-white p-6 rounded-xl shadow-md">
                  <h2 className="text-xl font-semibold mb-4 flex items-center text-purple-700">
                    <HelpCircle className="mr-2" /> Explain Plan
                  </h2>
                  <pre className="text-xs text-gray-700 bg-gray-50 p-3 rounded border whitespace-pre-wrap">
                    {response.explainPlan || "Plan mevcut değil."}
                  </pre>
                </section>
                <section className="bg-white p-6 rounded-xl shadow-md border-t-4 border-green-500">
                  <h2 className="text-xl font-semibold mb-4 flex items-center text-green-700">
                    <Zap className="mr-2" /> Performans Önerileri
                  </h2>
                  <div className="text-sm text-gray-700 whitespace-pre-wrap">
                    {response.performanceSuggestions || "Öneri yok."}
                  </div>
                </section>
              </div>
            </>
          )}
        </div>

        {/* Sidebar Tasks */}
        <aside className="space-y-6">
          <section className="bg-white p-6 rounded-xl shadow-md">
            <h2 className="text-xl font-semibold mb-4 flex items-center">
              <ListChecks className="mr-2 text-green-600" /> Günlük Görevler
            </h2>
            <ul className="space-y-3">
              {tasks.length > 0 ? tasks.map((task, i) => (
                <li key={i} className="p-3 bg-green-50 rounded-lg text-sm text-green-800 border border-green-100 cursor-pointer hover:bg-green-100 transition-colors"
                    onClick={() => handleTaskClick(task)}>
                  {task}
                </li>
              )) : <li className="text-gray-400 italic">Görevler yükleniyor...</li>}
            </ul>
          </section>

          <section className="bg-blue-50 p-6 rounded-xl border border-blue-100">
            <h3 className="font-bold text-blue-800 mb-2">Nasıl Çalışır?</h3>
            <ol className="text-sm text-blue-700 space-y-2 list-decimal ml-4">
              <li>Doğal dil ile istediğiniz veriyi tarif edin.</li>
              <li>AI bunu SQL'e dönüştürür ve PostgreSQL'de çalıştırır.</li>
              <li>Sorgunun performansını ve planını inceleyin.</li>
              <li>Yanlış sorgularda hatanın nedenini öğrenin.</li>
            </ol>
          </section>
        </aside>
      </main>
    </div>
  )
}

export default App
