
---

## ⚠️ 注意事項
1. **首次啟動時需要網路**，以取得 **台北動物園資料庫** 內容。
2. **AI 動物園助手** 需要 **網路連線** 才能使用。
3. 如果是透過Android stuido 部屬，需要設定Gemini 的 apkkey
    - 新增方法如下: 
    1. [點擊這裡取得 apiKey](https://aistudio.google.com/app/apikey?authuser=8&hl=zh-tw&_gl=1*dmdxms*_ga*NTI5MTE3OTcxLjE3NDIwNDQwMTQ.*_ga_P1DBVKWT6V*MTc0MjIyMjgxMi41LjEuMTc0MjIyMjgxNS41Ny4wLjEyNzMwOTExMzY.)
    2. 在位於專案的根目錄中的local.properties 檔案中插入下列代碼
    ```kotlin
        // Access your API key as a Build Configuration variable
        val apiKey = "your apkkey"
    ```
    


--- 
# 🦁 動物園簡介 APP

## 📌 主要功能介紹

<div style="display: flex; ; padding-left: 40px; padding-bottom: 20px">
  <img src="Readme%20Resource/P1.jpg" alt="我的圖片" width="300">
  <img src="Readme%20Resource/P3.jpg" alt="我的圖片" width="300">
</div>

1. **更換主頁選單列表**
   - 點擊 **左上角選單按鈕** 可開啟 **側邊選單 (DrawerLayout)**。
   - 透過 **切換 RecyclerView 的 Adapter** 來變更主頁列表內容。

---

<div style="display: flex; ; padding-left: 40px;; padding-bottom: 20px">
  <img src="Readme%20Resource/P4.jpg" alt="我的圖片" width="300">
</div>

2. **查看動物詳細資訊**
   - 點擊 **主頁面項目**，可開啟 **動物詳細介紹視窗**。
   - **DialogFragment** 顯示可 **上下滑動** 的詳細內容。

---

<div style="display: flex; ; padding-left: 40px;; padding-bottom: 20px">
  <img src="Readme%20Resource/P2.jpg" alt="我的圖片" width="300">
</div>

3. **AI 動物園助手 🤖**
   - 點擊 **AI 動物園助手按鈕**，可詢問與動物園相關的問題，例如：
     > "Where are the penguins?"
   - AI 助手將即時回應 **動物位置、習性等資訊**。

---


## 🔧 APP 技術架構
- **MVVM 架構**：按鈕控制以外的功能皆符合 MVVM 設計模式。
- **HILT 依賴注入**：
  - 提供 **離線儲存** 相關功能。
  - 方便 **單元測試 (Unit Test) 與開發維護**。

---

📱 **讓我們一起探索動物的奇妙世界吧！** 🏞️🐼