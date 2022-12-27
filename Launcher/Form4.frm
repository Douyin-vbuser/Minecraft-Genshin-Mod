VERSION 5.00
Begin VB.Form Form4 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Help"
   ClientHeight    =   4545
   ClientLeft      =   45
   ClientTop       =   390
   ClientWidth     =   5505
   Icon            =   "Form4.frx":0000
   LinkTopic       =   "Form4"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   4545
   ScaleWidth      =   5505
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.Frame Frame2 
      Caption         =   "配置MCI"
      Height          =   3255
      Left            =   120
      TabIndex        =   3
      Top             =   120
      Width           =   5295
      Begin VB.TextBox Text1 
         Height          =   2895
         Left            =   120
         Locked          =   -1  'True
         MultiLine       =   -1  'True
         ScrollBars      =   2  'Vertical
         TabIndex        =   4
         Text            =   "Form4.frx":038A
         Top             =   240
         Width           =   5055
      End
   End
   Begin VB.Frame Frame1 
      Caption         =   "问题未解决"
      Height          =   975
      Left            =   120
      TabIndex        =   0
      Top             =   3480
      Width           =   5295
      Begin VB.CommandButton Command2 
         Caption         =   "向作者邮件反映"
         Height          =   375
         Left            =   2880
         TabIndex        =   2
         Top             =   360
         Width           =   2055
      End
      Begin VB.CommandButton Command1 
         Caption         =   "提交问题到GitHub Issues"
         Height          =   375
         Left            =   240
         TabIndex        =   1
         Top             =   360
         Width           =   2415
      End
   End
End
Attribute VB_Name = "Form4"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Command1_Click()
Shell ("C:\Windows\explorer.exe https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/issues/new")
End Sub

Private Sub Command2_Click()
MsgBox "请向douyin_vbuser@outlook.com发送邮件", vbInformation, "邮箱已复制到剪切板"
Clipboard.Clear
Clipboard.SetText ("douyin_vbuser@outlook.com")
End Sub
