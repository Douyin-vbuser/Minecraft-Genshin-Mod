VERSION 5.00
Object = "{EAB22AC0-30C1-11CF-A7EB-0000C05BAE0B}#1.1#0"; "ieframe.dll"
Begin VB.Form Form3 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Download"
   ClientHeight    =   2505
   ClientLeft      =   150
   ClientTop       =   795
   ClientWidth     =   5205
   Icon            =   "Form3.frx":0000
   LinkTopic       =   "Form3"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   2505
   ScaleWidth      =   5205
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.TextBox Text8 
      Height          =   270
      Left            =   5760
      TabIndex        =   13
      Text            =   "Text8"
      Top             =   1920
      Width           =   4215
   End
   Begin VB.TextBox Text7 
      Height          =   270
      Left            =   5760
      TabIndex        =   12
      Text            =   "Text7"
      Top             =   1440
      Width           =   4215
   End
   Begin VB.TextBox Text6 
      Height          =   270
      Left            =   5760
      TabIndex        =   11
      Text            =   "Text6"
      Top             =   960
      Width           =   4215
   End
   Begin VB.TextBox Text5 
      Height          =   270
      Left            =   5760
      TabIndex        =   10
      Text            =   "Text5"
      Top             =   480
      Width           =   4215
   End
   Begin VB.Timer Timer3 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   5280
      Top             =   1440
   End
   Begin VB.Timer Timer2 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   5280
      Top             =   840
   End
   Begin VB.Timer Timer1 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   5280
      Top             =   240
   End
   Begin VB.Frame Frame2 
      Caption         =   "版本号"
      Height          =   2295
      Left            =   120
      TabIndex        =   1
      Top             =   120
      Width           =   5055
      Begin VB.TextBox Text4 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   9
         Text            =   "(none)"
         Top             =   1800
         Width           =   3615
      End
      Begin VB.TextBox Text3 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   8
         Text            =   "basic 1.0.6"
         Top             =   1320
         Width           =   3615
      End
      Begin VB.TextBox Text2 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   7
         Text            =   "alpha 1.1.7"
         Top             =   840
         Width           =   3615
      End
      Begin VB.TextBox Text1 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   6
         Text            =   "release 1.0.0"
         Top             =   360
         Width           =   3615
      End
      Begin VB.Label Label7 
         Caption         =   "地图："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   255
         Left            =   120
         TabIndex        =   5
         Top             =   1800
         Width           =   1455
      End
      Begin VB.Label Label5 
         Caption         =   "模组："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   255
         Left            =   120
         TabIndex        =   4
         Top             =   1320
         Width           =   1455
      End
      Begin VB.Label Label4 
         Caption         =   "启动器："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   255
         Left            =   120
         TabIndex        =   3
         Top             =   840
         Width           =   1455
      End
      Begin VB.Label Label3 
         Caption         =   "运行库："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   255
         Left            =   120
         TabIndex        =   2
         Top             =   360
         Width           =   1455
      End
   End
   Begin SHDocVwCtl.WebBrowser WebBrowser1 
      Height          =   1215
      Left            =   120
      TabIndex        =   0
      Top             =   360
      Width           =   5055
      ExtentX         =   8916
      ExtentY         =   2143
      ViewMode        =   0
      Offline         =   0
      Silent          =   0
      RegisterAsBrowser=   0
      RegisterAsDropTarget=   1
      AutoArrange     =   0   'False
      NoClientEdge    =   0   'False
      AlignLeft       =   0   'False
      NoWebView       =   0   'False
      HideFileNames   =   0   'False
      SingleClick     =   0   'False
      SingleSelection =   0   'False
      NoFolders       =   0   'False
      Transparent     =   0   'False
      ViewID          =   "{0057D0E0-3573-11CF-AE69-08002B2E1262}"
      Location        =   ""
   End
   Begin VB.Menu check 
      Caption         =   "Check"
   End
   Begin VB.Menu download 
      Caption         =   "Download"
   End
End
Attribute VB_Name = "Form3"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Declare Function URLDownloadToFile Lib "urlmon.dll" Alias "URLDownloadToFileA" (ByVal pCaller As Long, ByVal szURL As String, ByVal szFileName As String, ByVal dwReserved As Long, ByVal lpfnCB As Long) As Long

Private Sub check_Click()
Dim nUrl As String, F As String, S As Long
nUrl = "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/version-check/versions.txt": F = App.Path & "\versions.txt"
S = URLDownloadToFile(0, nUrl, F, 0, 0)
Open "" & App.Path & "\versions.txt" For Input As #3
While Not EOF(3)
Line Input #3, a
Line Input #3, b
Line Input #3, c
Line Input #3, d
Wend
Close #3

Text5.Text = a
Text6.Text = b
Text7.Text = c
Text8.Text = d

If a = Text1.Text And b = Text2.Text And c = Text3.Text And d = Text4.Text Then
    MsgBox "所有内容均为最新", vbInformation, "Update Manager"
Else
    If MsgBox("存在可用的更新,是否现在更新", vbQuestion + vbYesNo, "update Manager") = vbYes Then
        WebBrowser1.Navigate "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/updater/default.updater1.exe"
        Timer1.Enabled = True
    End If
End If
Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\versions.txt")
End Sub

Private Sub download_Click()
MsgBox "最好将文件下载至根目录", vbInformation, "Update Manager"
If Dir("" & App.Path & "\start.bat") = "" Then
    WebBrowser1.Navigate "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/full_version/default.minecraft.exe"
    Timer3.Enabled = True
Else
    WebBrowser1.Navigate "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/updater/default.updater1.exe"
    Timer1.Enabled = True
End If
End Sub

Private Sub Timer1_Timer()
If Dir("" & App.Path & "\updater.exe") = "" Then
    Form3.Caption = "Downloading..."
Else
    Shell "" & App.Path & "\default.updater.exe"
    Timer2.Enabled = True
    Timer1.Enabled = False
End If
End Sub

Private Sub Timer2_Timer()
If Dir("" & App.Path & "\updater\updater.exe") = "" Then
    Form3.Caption = "Unzipping..."
Else
    Shell "" & App.Path & "\updater\updater.exe"
    Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\default.updater.exe")
    Form3.Caption = "Download"
    Timer2.Enabled = False
End If
End Sub

Private Sub Timer3_Timer()
If Dir("" & App.Path & "\default.minecraft.exe") = "" Then
    Form3.Caption = "Downloading..."
Else
    Shell "" & App.Path & "\default.minecraft.exe"
    Timer3.Enabled = False
End If
End Sub
