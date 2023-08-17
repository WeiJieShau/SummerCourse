using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Windows;
using System.Windows.Controls;
using System.Threading;
using Newtonsoft.Json;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Markup;

namespace ServerSocket
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    /// 
    public class ClientData
    {
        public string Name { get; set; }
        public string IP { get; set; }
        public List<int> TestInt { get; set; }
        public Dictionary<int, string> TestDictionary { get; set; }
        [JsonIgnore]
        public Socket Socket { get; set; }
        [JsonIgnore]
        public string FullName
        {
            get
            {
                return $"{Name}({IP})";
            }
        }
    }
    public class SendData
    {
        public string Name { get; set; }
        
        public string Mes { get; set; }
        
    }
    public class ServerStatus
    {
        public string IP { get; set; }
        public int Port { get; set; }
        public Thread ListenThread { get; set; }

       

        public List<Socket> ConnectedClients { get; set; } = new List<Socket>();
        public List<ClientData> OnlineClient { get; set; } = new List<ClientData>();
    }
    
    public partial class MainWindow : Window
    {
        private ServerStatus curStatus = new ServerStatus();
        private Socket serverSocket;
        private bool isServerRunning = false;
        private List<Thread> receiveThreads = new List<Thread>();
        public MainWindow()
        {
            InitializeComponent();
        }

        private void bt_start_Click(object sender, RoutedEventArgs e)
        {
            // Read IP and Port from TextBoxes
            string ipAddress = tb_ip.Text;
            int port = int.Parse(tb_port.Text);

            // Initialize ServerStatus
            curStatus.IP = ipAddress;
            curStatus.Port = port;
            
            // Start the server
            StartServer();
        }

        private void bt_stop_Click(object sender, RoutedEventArgs e)
        {
            StopServer();
        }
        private void StartServer()
        {
            try
            {
                IPAddress ip = IPAddress.Parse(curStatus.IP);
                serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                serverSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, true);
                serverSocket.Bind(new IPEndPoint(ip, curStatus.Port));
                serverSocket.Listen(10);

                isServerRunning = true;

                Thread thread = new Thread(() => Listen(serverSocket, curStatus));
                curStatus.ListenThread = thread;
                thread.Start();

                // Update UI or perform other actions to indicate server started
                AppendMessage("Server started.");
            }
            catch (Exception ex)
            {
                AppendMessage("Error starting server: " + ex.Message);
            }
        }
        private void StopServer()
        {
            try
            {
                // Signal the listen thread to stop
                isServerRunning = false;

                AppendMessage("Server stopped.");
                Thread.Sleep(1000);
                SendToAllClients("Server", "Leave, please Disconnect");
                // Update UI or perform other actions to indicate server stopped

                




                // Close all client sockets
                /*
                foreach (Thread receiveThread in receiveThreads)
                {
                    receiveThread.Join(); // 等待线程结束
                }*/
                foreach (Socket clientSocket in curStatus.ConnectedClients)
                {
                    clientSocket.Close();
                }
                
                serverSocket.Close();

                
                
            }
            catch (Exception ex)
            {
                AppendMessage("Error stopping server: " + ex.Message);
            }
        }
        private void Listen(Socket curServer, ServerStatus curStatus)
        {
            while (isServerRunning)
            {
                try
                {
                    Socket client = curServer.Accept();
                    curStatus.ConnectedClients.Add(client);

                    Thread receive = new Thread(() => ReceiveMsg(client, curStatus));
                    receive.Start();
                    receiveThreads.Add(receive);
                }
                catch (Exception ex)
                {
                    if (isServerRunning)
                    {
                        Console.WriteLine("Error accepting client connection: " + ex.Message);
                    }
                }
            }
        }
        SendData clientmes = null;
        ClientData clientdata=null;
        private void ReceiveMsg(Socket clientSocket, ServerStatus curStatus)
        {
            try
            {

                while (clientSocket != null && clientSocket.Connected)
                {
                    Console.WriteLine("Waiting for data...");
                    string clientIP = ((IPEndPoint)clientSocket.RemoteEndPoint).Address.ToString();
                    byte[] result = new byte[clientSocket.Available];
                    int receive_num = clientSocket.Receive(result);

                    if (receive_num > 0)
                    {
                        string receive_str = Encoding.ASCII.GetString(result, 0, receive_num);

                        if (clientdata == null)
                        {
                            clientmes = JsonConvert.DeserializeObject<SendData>(receive_str);
                           
                        }

                        if (clientmes != null)
                        {
                            string Mes = clientmes.Name + " : " + clientmes.Mes;
                            
                            AppendMessage("("+clientIP+")"+Mes);

                            SendToAllClients(clientmes.Name, clientmes.Mes);
                            // Update UI or perform other operations with the received client data
                            
                        }
                        if (!clientSocket.Connected)
                        {
                            curStatus.ConnectedClients.Remove(clientSocket);
                            Console.WriteLine("Client disconnected. Removed from ConnectedClients list.");
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
            finally
            {
                clientSocket.Close();
                Console.WriteLine("Socket closed.");
            }

        }
        private void AppendMessage(string message)
        {
            Dispatcher.Invoke(() =>
            {
                tblock_mes.Text += message + Environment.NewLine;
            });
            
        }
        
        private void SendToAllClients(String name,String mes)
        {
            SendData sendDataObj = new SendData
            {
                Name = name,
                
                Mes = mes
            };
            
            
            foreach (Socket clientSocket in curStatus.ConnectedClients)
            {
                try
                {
                    
                    string jsonData = JsonConvert.SerializeObject(sendDataObj);
                    byte[] sendData = Encoding.ASCII.GetBytes(jsonData);
                    clientSocket.Send(sendData);
                }
                catch (Exception ex)
                {
                    
                    Console.WriteLine("Error sending message to client: " + ex.Message);
                }
            }
        }
    }
}
